Description :
Build a tag cloud infrastructure, should scan all the documents in a folder or any document shared through API. Build apis that can take one document or set of documents, apis should read through the documents. If there is PDF, it should read PDF (any open source PDF library can be used). For each word, build the count based on the repeatability across the documents. Key is to ensure that this code ensures that read/write sequences are taken care and is thread safe. Extra bonus for it to work across the clusters.  Please look for right data structure to implement the above.
     
a) API to upload the document or set of documents, should read the word and update central word count. Word reading should be fuzzy and should handle spelling errors as well.
     b) API to fetch the word and counts, sorted by count and then word
     c) API where a word is given, and gives list of words near to the word and their count. (String distance)



Api URL :
post : http://localhost:8989/get-valid-spell-word-count
post : http://localhost:8989/near-by-word
post : http://localhost:8989/fetch-word-count-sort
