import unittest
from classifyer import *
class TestCustomModel(unittest.TestCase):

    model = None
    def setUp(self):
        self.model = CustomModel()

    def test_initializeGraph(self):
        self.assertTrue(self.model.g != None)
        self.assertTrue(self.model.sess != None)
        self.assertTrue(self.model.input_values != None)
        self.assertTrue(self.model.predictions != None)

    def test_loadFiles(self):
         
        # run load files
        self.model.loadFiles()

        self.assertTrue(self.model.labelMap != None)
        self.assertTrue(self.model.labelDict != None and len(self.model.labelDict) > 0)

    def test_predict(self):
        self.model.loadFiles()

        labels = self.model.predict('desert.jpg')
        self.assertTrue(labels != None and len(labels) > 0)

if __name__ == '__main__':
    unittest.main()