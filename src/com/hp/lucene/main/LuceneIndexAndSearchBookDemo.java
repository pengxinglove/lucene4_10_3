package com.hp.lucene.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.hp.lucene.model.Book;

public class LuceneIndexAndSearchBookDemo {
	public static String indexPath = "E:\\lucene_index\\";
	static String fieldName = "description";
	public static void main(String[] args){
		try {
			createIndex();
			searchIndex();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void searchIndex() throws IOException, ParseException {
	
		 //检索内容
		String text = "IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法。";
		
		//实例化IKAnalyzer分词器
		Analyzer analyzer = new IKAnalyzer(true);
		
		Directory directory = null;
		IndexWriter iwriter = null;
		IndexReader ireader = null;
		IndexSearcher isearcher = null;
		//搜索过程**********************************
	    //实例化搜索器   
		File indexFile = new File(indexPath);
	    directory = FSDirectory.open(indexFile);
		ireader = DirectoryReader.open(directory);
		isearcher = new IndexSearcher(ireader);			
		
		String keyword = "面积和周长";			
		//使用QueryParser查询分析器构造Query对象
		QueryParser qp = new QueryParser(Version.LUCENE_4_10_3, fieldName,  analyzer);
		qp.setDefaultOperator(QueryParser.OR_OPERATOR);
		Query query = qp.parse(keyword);
		System.out.println("Query = " + query);
		
		//搜索相似度最高的5条记录
		TopDocs topDocs = isearcher.search(query , 5);
		System.out.println("命中：" + topDocs.totalHits);
		//输出结果
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (int i = 0; i < topDocs.totalHits; i++){
			Document targetDoc = isearcher.doc(scoreDocs[i].doc);
			System.out.println("内容：" + targetDoc.toString());
		}		
	}
	
	public static void createIndex() throws Exception {
	    // 采集数据
	  
	    List<Book> list = queryBooks();

	    // 将采集到的数据封装到Document对象中
	    List<Document> docList = new ArrayList<>();
	    Document document;
	    for (Book book : list) {
	        document = new Document();
	        // store:如果是yes，则说明存储到文档域中
	        // 图书ID
	        // 不分词、索引、存储 StringField
	        Field id = new StringField("id", book.getId().toString(), Store.YES);
	        // 图书名称
	        // 分词、索引、存储 TextField
	        Field name = new TextField("name", book.getName(), Store.YES);
	        // 图书价格
	        // 分词、索引、存储 但是是数字类型，所以使用FloatField
	    //    Field price = new FloatField("price", book.getPrice(), Store.YES);
	        // 图书图片地址
	        // 不分词、不索引、存储 StoredField
	        Field pic = new StoredField("pic", book.getPic());
	        // 图书描述
	        // 分词、索引、不存储 TextField
	        Field description = new TextField("description",
	                book.getDescription(), Store.YES);

	        // 设置boost值
	        if (book.getId() == 4)
	            description.setBoost(100f);

	        // 将field域设置到Document对象中
	        document.add(id);
	        document.add(name);
	     //  document.add(price);
	        document.add(pic);
	        document.add(description);

	        docList.add(document);
	    }

	    // 创建分词器，标准分词器
	    // Analyzer analyzer = new StandardAnalyzer();
	    // 使用ikanalyzer
	    Analyzer analyzer = new IKAnalyzer();

	    // 创建IndexWriter
	    IndexWriterConfig cfg = new IndexWriterConfig(Version.LUCENE_4_10_3,
	            analyzer);
	    // 指定索引库的地址
	    File indexFile = new File(indexPath);
	    Directory directory = SimpleFSDirectory.open(indexFile);
	    IndexWriter writer = new IndexWriter(directory, cfg);

	    // 通过IndexWriter对象将Document写入到索引库中
	    for (Document doc : docList) {
	        writer.addDocument(doc);
	    }

	    // 关闭writer
	    writer.close();
	}

	private static List<Book> queryBooks() {
		List<Book> books = new ArrayList<Book>();
		Book book = new Book();
		book.setId(1);
		book.setName("1年级下册");
		book.setPic("c:\\学语文1年级下册.png");
		book.setDescription("人教小学语文1年级下册(家教机)(6.3).drm");
		books.add(book);
		
		 book = new Book();
		book.setId(2);
		book.setName("3年级下册");
		book.setPic("c:\\春小学语文3年级下册.png");
		book.setDescription("长春小学语文3年级下册(5.1)");
		books.add(book);
		
		 book = new Book();
		book.setId(3);
		book.setName("第11册");
		book.setPic("c:\\改革实验教材语文第11册.png");
		book.setDescription("北京市义务教育课程改革实验教材语文第11册(5.2)");
		books.add(book);
		
		 book = new Book();
		book.setId(4);
		book.setName("2年级下册");
		book.setPic("c:\\小学语文2年级下.png");
		book.setDescription("人教小学语文2年级下册(家教机)(5.5)");
		books.add(book);
		
		book = new Book();
		book.setId(5);
		book.setName("面积");
		book.setPic("c:\\面积.png");
		book.setDescription("三角形的面积(5.4)");
		books.add(book);
		
		book = new Book();
		book.setId(6);
		book.setName("周长");
		book.setPic("c:\\周长.png");
		book.setDescription("学习周长与面积的应用题");
		books.add(book);
		
		return books;
	}
}
