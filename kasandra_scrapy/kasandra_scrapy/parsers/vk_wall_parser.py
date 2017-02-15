# -*- coding: utf-8 -*-
import json
import re
from .vk_wall_item import VkWallItem


class VkWallParser(object):
    def __init__(self):
        self.__regex = "(https?://[^\\s/$.?#].[^\\s]*)"

    def parse(self, json_data):
        data = json.loads(json_data)
        items = data['response']['items']

        result = []

        for item in items:
            date = item['date']
            text = item['text']

            urls = set(re.findall(self.__regex, text))

            try:
                for attachment in item['attachments']:
                    if attachment['type'] == 'link':
                        urls.add(attachment['link']['url'])
            except:
                pass

            for url in urls:
                wall_item = VkWallItem(url, date)
                result.append(wall_item)

        return result
