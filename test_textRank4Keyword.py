from unittest import TestCase
import string
from collections import OrderedDict
import numpy as np
import spacy
from nltk import word_tokenize, re
from spacy.lang.en.stop_words import STOP_WORDS
from nltk.stem import WordNetLemmatizer
from SearchInput import *

class TestTextRank4Keyword(TestCase):

    def test_sentence_segment(self):
        test_nlp = spacy.load('en_core_web_sm')
        test_class = TextRank4Keyword()
        test_false_argument = ". / ] [ , hello in during this which 0 1223 I"
        doc = test_nlp(test_false_argument)
        actual_return_list = test_class.sentence_segment(doc, ['NOUN', 'VERB', 'ADJ', 'ADV'], False)
        assert (len([item for sublist in actual_return_list for item in sublist]) == 0)


    def test_get_vocab(self):
        test_nlp = spacy.load('en_core_web_sm')
        test_class = TextRank4Keyword()

        test_argument = "A boy play football everyday"
        doc = test_nlp(test_argument)
        test_sentences = test_class.sentence_segment(doc, ['NOUN', 'VERB', 'ADJ', 'ADV'], False)
        actual_return_list = test_class.get_vocab(test_sentences)
        assert (len([i for i in list(actual_return_list.values()) if i != (range(0,4)).index(i)]) == 0)


    def test_get_token_pairs(self):
        test_nlp = spacy.load('en_core_web_sm')
        test_class = TextRank4Keyword()

        test_argument = "child play football"
        expected_output = [('child', 'play'), ('child', 'football'), ('play', 'football')]
        doc = test_nlp(test_argument)
        test_sentences = test_class.sentence_segment(doc, ['NOUN', 'VERB', 'ADJ', 'ADV'], False)
        actual_return_list = test_class.get_token_pairs(4, test_sentences)
        assert (len([i for i, j in zip(actual_return_list, expected_output) if i != j]) == 0)

    def test_symmetrize(self):
        test_nlp = spacy.load('en_core_web_sm')
        test_class = TextRank4Keyword()

        test_argument = np.asarray([[1, 2, 3], [4, 5, 6], [7, 8, 9]], dtype=np.int16)
        expected_output = np.asarray([[1, 6, 10], [6, 5, 14], [10, 14, 9]], dtype=np.int16)
        actual_return_list = test_class.symmetrize(test_argument)
        assert (len([x for x in actual_return_list if x not in expected_output] + [x for x in expected_output if x not in actual_return_list]) == 0)

    def test_cleanData(self):
        test_nlp = spacy.load('en_core_web_sm')
        test_class = TextRank4Keyword()

        test_argument = "A child  [. () * < > plays football, , , [] . 12 rt a with his friends and sr lt not with his other friends"
        expected_output = "child play football with his friend and not with his other friend"
        actual_output = test_class.cleanData(test_argument)
        assert expected_output == actual_output
