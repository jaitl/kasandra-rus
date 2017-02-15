# -*- coding: utf-8 -*-
import scrapy
import json
from furl import furl
from yaml import load
import os
from ..parsers import vk_wall_parser


class VkWallParser(scrapy.Spider):
    name = "wall"
    wall_max_count = 100
    api_url = 'https://api.vk.com/method/wall.get'

    def start_requests(self):
        configs = self.load_config("configs/vk-wall.yaml")
        print(configs)
        params = {
            "v": "5.53",
            "domain": "test",
            "count": 100,
            "offset": 0,
            "filter": "owner"
        }

        yield self.create_request(params, self.parse)

    def parse(self, response):
        params = dict(response.meta['params'])
        parser = vk_wall_parser.VkWallParser()
        data = response.text
        wall_items = parser.parse(data)

        count_items = self.count_items(data)

        if count_items == self.wall_max_count:
            params['offset'] += self.wall_max_count
            yield self.create_request(params, self.parse)

    def count_items(self, json_data):
        data = json.loads(json_data)
        items = data['response']['items']
        return len(items)

    def create_request(self, params, callback):
        url = furl(self.api_url).add(params).url
        request = scrapy.Request(url=url, callback=callback, meta={'params': params})
        return request

    def load_config(self, path):
        work_dir = os.path.dirname(__file__)
        filename = os.path.join(work_dir, path)
        with open(filename, 'r') as stream:
            configs = load(stream)
        return configs