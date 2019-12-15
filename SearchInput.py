# from nltk.corpus import stopwords
# from nltk.corpus import stopwords
# from nltk.tokenize import word_tokenize
# import string
# from nltk.stem import PorterStemmer
# from nltk.tokenize import sent_tokenize, word_tokenize
# from nltk.stem import WordNetLemmatizer
# from nltk.corpus import wordnet
#
#
# ps = PorterStemmer()
# lemmatizer = WordNetLemmatizer()
#
#
# example_sent = "Karol plays flute at night [] 4 32/ 5 .< > << [] }{ . . . . . . "
# stop_words = set(stopwords.words('english'))
#
# word_tokens = word_tokenize(example_sent)
#
# filtered_sentence = [w for w in word_tokens if not w in stop_words and w not in string.punctuation]
# filtered_sentence_a = []
# filtered_sentence_b = []
# filtered_sentence_c = []
# for w in filtered_sentence:
#     # filtered_sentence_b.append(lemmatizer.lemmatize(w, pos='a'))
#     for syn in wordnet.synsets(lemmatizer.lemmatize(w)):
#         for l in syn.lemmas():
#             filtered_sentence_b.append(l.name())
#         # filtered_sentence_b.append(y.name())
#     # filtered_sentence_a.append(ps.stem(w))
#     # filtered_sentence_c.append(lemmatizer.lemmatize(ps.stem(w)))
#
# print(word_tokens)
# # print(filtered_sentence_a)
# print(filtered_sentence_b)
# # print(filtered_sentence_c)
# #
# # # print(lemmatizer.lemmatize("This"))
# # # print(lemmatizer.lemmatize("sample"))
# # # print(lemmatizer.lemmatize("sentence"))
# # # print(lemmatizer.lemmatize("showing"))
# # # print(lemmatizer.lemmatize("stop"))
# # # print(lemmatizer.lemmatize("words"))
# # # print(lemmatizer.lemmatize("filtration"))
# #
# #
import string
from collections import OrderedDict
import numpy as np
import spacy
from nltk import word_tokenize, re
from spacy.lang.en.stop_words import STOP_WORDS
from nltk.stem import WordNetLemmatizer
# from nltk.corpus import wordnet
# from nltk.tag.stanford import StanfordNERTagger





class TextRank4Keyword():
    """Extract keywords from text"""

    def __init__(self):
        self.d = 0.85  # damping coefficient, usually is .85
        self.min_diff = 1e-5  # convergence threshold
        self.steps = 10  # iteration steps
        self.node_weight = None  # save keywords and its weight
        self.lemmatizer=WordNetLemmatizer()  # lemmatizer for cleaning data
        self.nlp = spacy.load('en_core_web_sm')

        # self.st = StanfordNERTagger('stanford-ner/all.3class.distsim.crf.ser.gz', 'stanford-ner/stanford-ner.jar')

    def set_stopwords(self, stopwords):
        """Set stop words"""
        for word in STOP_WORDS.union(set(stopwords)):
            lexeme = self.nlp.vocab[word]
            lexeme.is_stop = True

    def sentence_segment(self, doc, candidate_pos, lower):
        """Store those words only in cadidate_pos"""
        sentences = []
        for sent in doc.sents:
            selected_words = []
            for token in sent:
                # Store words only with cadidate POS tag
                if token.pos_ in candidate_pos and token.is_stop is False:
                    if lower is True:
                        selected_words.append(token.text.lower())
                    else:
                        selected_words.append(token.text)
            sentences.append(selected_words)
        return sentences

    def get_vocab(self, sentences):
        """Get all tokens"""
        vocab = OrderedDict()
        i = 0
        for sentence in sentences:
            for word in sentence:
                if word not in vocab:
                    vocab[word] = i
                    i += 1
        return vocab

    def get_token_pairs(self, window_size, sentences):
        """Build token_pairs from windows in sentences"""
        token_pairs = list()
        for sentence in sentences:
            for i, word in enumerate(sentence):
                for j in range(i + 1, i + window_size):
                    if j >= len(sentence):
                        break
                    pair = (word, sentence[j])
                    if pair not in token_pairs:
                        token_pairs.append(pair)
        return token_pairs

    def symmetrize(self, a):
        print(a)
        print('-----')
        print(a.T)
        print('-----')
        print(np.diag(a.diagonal()))
        print('-----')
        print(a + a.T - np.diag(a.diagonal()))
        return a + a.T - np.diag(a.diagonal())

    def get_matrix(self, vocab, token_pairs):
        """Get normalized matrix"""
        # Build matrix
        vocab_size = len(vocab)
        g = np.zeros((vocab_size, vocab_size), dtype='float')
        for word1, word2 in token_pairs:
            i, j = vocab[word1], vocab[word2]
            g[i][j] = 1

        # Get Symmeric matrix
        g = self.symmetrize(g)

        # Normalize matrix by column
        norm = np.sum(g, axis=0)
        g_norm = np.divide(g, norm, where=norm != 0)  # this is ignore the 0 element in norm

        return g_norm

    def get_keywords(self, number=10):
        """Print top number keywords"""
        node_weight = OrderedDict(sorted(self.node_weight.items(), key=lambda t: t[1], reverse=True))
        for i, (key, value) in enumerate(node_weight.items()):
            if i > number:
                break

    def analyze(self, text,
                candidate_pos=['NOUN', 'PROPN'],
                window_size=4, lower=False, stopwords=list()):
        """Main function to analyze text"""

        # Set stop words
        stopwords.append('arg, com, org, net')
        self.set_stopwords(stopwords)

        # Clean the sentence
        text = self.cleanData(text)

        # Parse text by spaCy
        doc = self.nlp(text)

        # Filter sentences
        sentences = self.sentence_segment(doc, candidate_pos, lower)  # list of list of words

        # Build vocabulary
        vocab = self.get_vocab(sentences)

        # Get token_pairs from windows
        token_pairs = self.get_token_pairs(window_size, sentences)

        # Get normalized matrix
        g = self.get_matrix(vocab, token_pairs)

        # Initionlization for weight(pagerank value)
        pr = np.array([1] * len(vocab))

        # Iteration
        previous_pr = 0
        for epoch in range(self.steps):
            pr = (1 - self.d) + self.d * np.dot(g, pr)
            if abs(previous_pr - sum(pr)) < self.min_diff:
                break
            else:
                previous_pr = sum(pr)

        # Get weight for each node
        node_weight = dict()
        for word, index in vocab.items():
            node_weight[word] = pr[index]

        self.node_weight = node_weight

    def cleanData(self, input_str):
        input_str = input_str.lower()
        input_str = re.sub(r'\d+', '', input_str)
        input_str = input_str.translate(str.maketrans("", "", string.punctuation))
        input_str = input_str.strip()
        input_str = word_tokenize(input_str)
        input_str = [self.lemmatizer.lemmatize(i) for i in input_str]
        input_str = [i for i in input_str if len(i) > 2]
        input_str = ' '.join(input_str)
        return input_str


text = '''
Child and dog are playing.
'''
tr4w = TextRank4Keyword()

tr4w.analyze(text, candidate_pos = ['NOUN', 'PROPN', 'VERB', 'ADJ', 'ADV'], window_size=4, lower=False)
tr4w.get_keywords(10)
