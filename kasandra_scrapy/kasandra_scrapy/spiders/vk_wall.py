# -*- coding: utf-8 -*-
import scrapy
from furl import furl
from parsers import vk_wall_parser


class VkWallParser(scrapy.Spider):
    name = "wall"

    def start_requests(self):
        api_url = 'https://api.vk.com/method/wall.get'
        params = {
            "v": "5.53",
            "domain": "tj",
            "count": 3,
            "offset": 0
        }

        url = furl(api_url).add(params).url

        yield scrapy.Request(url=url, callback=self.parse)

    def parse(self, response):
        parser = vk_wall_parser.VkWallParser
        data = response.text
        wall_raw = parser.parse(data)
