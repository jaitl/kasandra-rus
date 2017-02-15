# -*- coding: utf-8 -*-
import unittest
from ..vk_wall_parser import VkWallParser


class VkWallParserTest(unittest.TestCase):
    def test_10_response(self):
        with open("parsers/tests/test_data/vk/response10.json", "r", encoding="utf-8") as file:
            json = file.read()

        parser = VkWallParser()
        results = parser.parse(json)

        test_url = "https://russian.rt.com/article/314862-brifing-minoborony-rf-po-situacii-v-sirii"
        test_date = 1470063137

        result_items = list(filter(lambda x: x.url == test_url and x.date == test_date, results))

        self.assertEqual(len(results), 10)
        self.assertEqual(len(result_items), 1)


if __name__ == '__main__':
    unittest.main()
