package app.test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;

import app.common.net.entities.SourceInfo;
import app.datamodel.PojoCursor;
import app.datamodel.PojoManager;
import app.datamodel.StorablePojoCursor;
import app.datamodel.StorablePojoManager;
import app.datamodel.mongo.DBManager;
import app.datamodel.pojos.User;

public class Test
{
	public static void main(String[] args)
	{
		setupDBManager();
		
		

		/*PojoManager<SourceInfo> manager = new PojoManager<SourceInfo>(SourceInfo.class, "Sources");
		PojoCursor<SourceInfo> cursor = manager.aggregate(Arrays.asList(Aggregates.project(Projections.fields(Projections.excludeId(), Projections.include("enabled"), Projections.computed("name", "$_id")))));
		while (cursor.hasNext()) {
			SourceInfo info = cursor.next();
			System.out.println("Name: " + info.getName());
			System.out.println("Enabled: " + info.isEnabled());
			System.out.println("---------------");
		}
		printPhase("DONE!");
		/*
		StorablePojoManager<First> firstManager = new StorablePojoManager<First>(First.class);
		firstManager.drop();
		printPhase("CREATE");
		Second second1 = new Second("insideA", "lol", 100);
		Second second2 = new Second(null, "lol", 200);
		Second second3 = new Second("insideC", "xd", 300);
		First first1 = new First("aaaaaaa", "fuck", 10, second1);
		First first2 = new First("bbbbbbb", "pussy", 20, second2);
		First first3 = new First("ccccccc", null, 20, second3);
		first2.getSeconds().add(new Second("insideListB", "abc", 1));
		first2.getSeconds().add(new Second("insideListB2", "def", 1));
		first2.getSeconds().add(new Second("insideListB3", "ghi", 2));
		print(first1); print(first2); print(first3);
		printPhase("SAVE");
		firstManager.save(first1, first2, first3);
		print(first1); print(first2); print(first3);
		printPhase("READ FROM DB");
		HashMap<String, Object> fields = new HashMap<String, Object>();
		StorablePojoCursor<First> firstCursor = (StorablePojoCursor<First>)firstManager.find();
		List<First> firstList = new ArrayList<First>();
		while (firstCursor.hasNext())
			firstList.add(firstCursor.next());
		for (First f: firstList)
			print(f);
		printPhase("EDIT");
		firstList.get(2).setIntField(200);
		firstList.get(2).setStrField("fregna");
		firstList.get(0).getSecond().setInInt(999);
		firstList.get(0).getSecond().setInStr(null);
		firstList.get(0).setStrField("sex");
		firstList.get(1).setIntField(123);
		firstList.get(1).getSeconds().get(1).setInInt(123);
		firstList.get(1).getSeconds().get(1).setInStr("qwertyuiop");
		firstList.get(1).getSeconds().get(2).delete();
		firstList.get(1).getSeconds().add(new Second("added", "pushed", 100));
		firstList.get(1).getSeconds().add(new Second("added2", "pushed2", 200));
		for (First f: firstList)
			print(f);
		printPhase("SAVE (UPDATE)");
		firstManager.save(firstList);
		for (First f: firstList)
			print(f);
		printPhase("!!! DONE !!!");
		System.out.println(Instant.now());*/
	}
	
	public static void printPhase(String phase)
	{
		System.out.println();
		System.out.println("##########################################");
		System.out.print("#");
		for (int i = 0; i < 20 - phase.length()/2; i++)
			System.out.print(" ");
		System.out.print(phase);
		for (int i = 0; i < 20 - phase.length()/2 - 1; i++)
			System.out.print(" ");
		if (phase.length() % 2 == 0)
			System.out.print(" ");
		System.out.println("#");
		System.out.println("##########################################");
		System.out.println();
	}
	
	public static void print(First first)
	{
		print(first, 0);
	}
	
	public static void print(Second second)
	{
		print(second, 0);
	}
	
	public static void print(Third third)
	{
		print(third, 0);
	}
	
	public static void print(First first, int indent)
	{
		if (first == null)
			return;
		String prefix = "";
		for (int i = 0; i < indent; i++)
			prefix += "\t";
		System.out.println(prefix + "TYPE: FIRST");
		System.out.println(prefix + "id: " + first.getId());
		System.out.println(prefix + "strField: " + first.getStrField());
		System.out.println(prefix + "intField: " + first.getIntField());
		System.out.println(prefix + "second:"); print(first.getSecond(), indent+1);
		System.out.println(prefix + "seconds:"); for (Second second: first.getSeconds()) print(second, indent+1);
		System.out.println(prefix + "STATE: " + first.getState() + (first.isDeleted() ? " (deleted)" : "") + (first.isDeleting() ? " (deleting)" : ""));
		if (indent == 0) {
			System.out.println("***********************************");
			System.out.println();
		} else {
			System.out.println(prefix + "-----");
		}
	}

	public static void print(Second second, int indent)
	{
		if (second == null)
			return;
		String prefix = "";
		for (int i = 0; i < indent; i++)
			prefix += "\t";
		System.out.println(prefix + "TYPE: SECOND");
		System.out.println(prefix + "name: " + second.getName());
		System.out.println(prefix + "inStr: " + second.getInStr());
		System.out.println(prefix + "inInt: " + second.getInInt());
		System.out.println(prefix + "STATE: " + second.getState() + (second.isDeleted() ? " (deleted)" : "") + (second.isDeleting() ? " (deleting)" : ""));
		if (indent == 0) {
			System.out.println("***********************************");
			System.out.println();
		} else {
			System.out.println(prefix + "-----");
		}
	}

	public static void print(Third third, int indent)
	{
		if (third == null)
			return;
		String prefix = "";
		for (int i = 0; i < indent; i++)
			prefix += "\t";
		System.out.println(prefix + "TYPE: THIRD");
		System.out.println(prefix + "name: " + third.getName());
		System.out.println(prefix + "inStr: " + third.getInStr());
		System.out.println(prefix + "inInt: " + third.getInInt());
		System.out.println(prefix + "STATE: " + third.getState() + (third.isDeleted() ? " (deleted)" : "") + (third.isDeleting() ? " (deleting)" : ""));
		if (indent == 0) {
			System.out.println("***********************************");
			System.out.println();
		} else {
			System.out.println(prefix + "-----");
		}
	}
	public static void setupDBManager()
	{
		DBManager.setHostname("127.0.0.1");
		DBManager.setPort(27017);
		DBManager.setUsername("root");
		DBManager.setPassword("rootpass");
		DBManager.setDatabase("mydb");
	}

}
