import unittest
from app.src.app import my_function


class MyTest(unittest.TestCase):
    def test_my_function(self):
        self.assertEqual(my_function(1, 1), 2)
        self.assertEqual(my_function(1, -1), 0)
        self.assertEqual(my_function(0, 0), 0)
        self.assertEqual(my_function(-1, -1), -2)
        self.assertEqual(my_function(1.0, 1), 2)        
        self.assertEqual(my_function(1.1, 1.1), 2.2)        
        
        
        
if __name__ == '__main__':
    unittest.main()        
