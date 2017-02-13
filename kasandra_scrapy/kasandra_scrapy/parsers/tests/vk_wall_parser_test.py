# -*- coding: utf-8 -*-
import unittest
from ..vk_wall_parser import VkWallParser

class VkWallParserTest(unittest.TestCase):
    def test_10_response(self):
        with open("text_data/vk/vk_wall_parser.json", "r", encoding="utf-8") as file:
            json = file.read()

        parser = VkWallParser()
        result = parser.parse(json)

        self.assertEqual(len(result), 10)



if __name__ == '__main__':
    unittest.main()
