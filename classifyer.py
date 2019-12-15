from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
from google.cloud import storage
from PIL import Image
import tensorflow as tf
import time
import base64
import json
import matplotlib.pyplot as plt
import base64
import io
from matplotlib import pyplot as plt
import matplotlib.image as mpimg


model = None
BUCKET_NAME = 'first_bucket2'
LABEL_MAP_PATH = 'classes-trainable.txt'
LABEL_DICT_PATH = 'class-descriptions.csv'
MODEL_PATH = './saved_model.ckpt/saved_model.ckpt'
TOP_K = 10
SCORE_THRESHOLD = 0.6

class CustomModel():
    
    g = None
    sess = None
    labelMap = None
    labelDict = None
    input_values = None
    predictions = None
    def __init__(self):
        # global MODEL_PATH
        tf.compat.v1.disable_eager_execution()
        self.g = tf.Graph()
        with self.g.as_default():
            self.sess = tf.compat.v1.Session()
            
            saver = tf.compat.v1.train.import_meta_graph(MODEL_PATH + '.meta')
            saver.restore(self.sess, MODEL_PATH)
            self.input_values = self.g.get_tensor_by_name('input_values:0')
            self.predictions = self.g.get_tensor_by_name('multi_predictions:0')

    def loadFiles(self):
        # global LABEL_MAP_PATH, LABEL_DICT_PATH
        labelmap = [line.rstrip() for line in tf.io.gfile.GFile(LABEL_MAP_PATH)]
        label_dict = {}
        for line in tf.io.gfile.GFile(LABEL_DICT_PATH):
            words = [word.strip(' "\n') for word in line.split(',', 1)]
            label_dict[words[0]] = words[1]

        self.labelMap = labelmap
        self.labelDict = label_dict
    
    def predict(self, image):
        # compressed_image = tf.compat.v1.io.gfile.GFile(image_filename, 'rb').read()
        
        start = time.time()
        predictions_eval = self.sess.run(
            self.predictions, feed_dict={
                self.input_values: [image]
            })
        end = time.time()
        print('elapsed = ', (end - start))
        top_k = predictions_eval.argsort()[::-1]  # indices sorted by score
        if TOP_K > 0:
          top_k = top_k[:TOP_K]
        if SCORE_THRESHOLD is not None:
          top_k = [i for i in top_k
                   if predictions_eval[i] >= SCORE_THRESHOLD]
        print('Image: \n')
        for idx in top_k:
          mid = self.labelMap[idx]
          display_name = self.labelDict[mid]
          score = predictions_eval[idx]
          print('{:04d}: {} - {} (score = {:.2f})'.format(
              idx, mid, display_name, score))
        return top_k

def handler(request):
    global model
    if model == None:
        model = CustomModel()
        model.loadFiles()
    image = decodeStringToBytes(request['img'])
    model.predict(image)
 
def encodeBytesToString(b):
    return base64.b64encode(b).decode('utf-8')

def decodeStringToBytes(encoded):
    string = encoded.encode('utf-8')
    return base64.decodebytes(string)

def img_to_json(image_filename):
    with open(image_filename, "rb") as image:
        f = image.read()
        b = bytearray(f)
        
    string = encodeBytesToString(f)
    
    data = {}
    data['id'] = image_filename
    data['img'] = string
    json_data = json.dumps(data)
    decoded = decodeStringToBytes(string)
    return data

if __name__ == '__main__':
    j = img_to_json('desert.jpg')
    
    with open('test.out', "w") as res:
        res.write(j['img'])
    # image = j['img']
    # i = base64.b64decode(image)
    # i = io.BytesIO(i)
    # print(i)
    # i = mpimg.imread(i, format='JPG')

    # plt.imshow(i, interpolation='nearest')
    # plt.show()
    handler(j)
    # handler(None)
#   tf.compat.v1.app.run()


