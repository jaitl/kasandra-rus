from pymystem3 import Mystem

mystem = Mystem(entire_input=False)

def lemmatize(text):
	return mystem.lemmatize(text)