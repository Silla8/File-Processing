package assignment3;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Process {

	static int index= 0;
	static int nomberOfIndentation=0;
	static String[] entries;
	static String destination;
	
	public static void main(String... args) throws Exception{
	
		destination(args);
		fileCopy(args);
		
		
	}
	
	
	
	static void fileCopy(String... args)throws Exception{
		
		for(String src: args)
		{
			if(src.equals(destination)) break; // avoid processing the destination folder if given
			int index= cuttingPath(src); // obtaining the index of the farest directory in the given path
			Path path= Paths.get(src); 
			if(Files.exists(path) && Files.isDirectory(path))
			{
				indentation();
				System.out.println(">Started: "+src);
				List<Path> listOfPath=Files.list(path).collect(Collectors.toList()); // obtaining elements inside a directory
				String[] arrayOfPath= listToString(listOfPath);
				nomberOfIndentation++;
				fileCopy(arrayOfPath); // recursive file processing
				nomberOfIndentation--;
				indentation();
				System.out.println(">Finished FOLDER: "+src);
				
			}
			else if(Files.exists(path) && !Files.isDirectory(path))
			{
				
				Path target=resolution(path, index); // creating the destination path based on the last directory 
				Files.createDirectories(target);
				indentation();
				System.out.println(">Started: "+src);
				int bites=fileProcessing(target,path, src); // Processing center
				indentation();
				System.out.println(">Finished FILE: "+src);
				indentation();
				System.out.printf(">Total %s were copied!\n", biteConversion(bites, 1));
				
			}
			else 
			{
				try
				{
					throw new FileNotFoundException("The file or directory : "+path+" does not exist in the origin.");
				}catch(FileNotFoundException e)
				{
					System.err.println(e.getMessage());
				}
			}
			
		}
		
		
	}

	static void indentation() {
		for(int i=0; i<nomberOfIndentation; i++) System.out.print("\t"); ////////// indentation
	}
	
	static String biteConversion(double bites, int flag)
	{
		if(bites<1024)
		{
			if(bites>Math.floor(bites))
			{
				bites=Math.ceil(bites);
			}
			return String.format("%d%s",(int)bites,flag(flag));	
		}else
		{
			flag++;
			bites=bites/1024;
			return biteConversion(bites, flag);
		}
		
	}
	
	static String flag(int flag)
	{
		String string="";
		switch(flag)
		{
		case 1: string="B";
		break;
		case 2: string="KB";
		break;
		case 3: string="MB";
		break;
		case 4: string="GB";
		break;
		case 5: string="TB";
		break;
		}
		
		return string;
	}
	
	static String[] listToString(List<Path> list)
	{
		int size= list.size();
		String[] str= new String[size];
		for(int i=0; i<size; i++)
		{
			str[i]= list.get(i).toString();
		}
		return str;
	}

	static Path resolution(Path path, int index)
	{
		int limit= path.getNameCount();
		StringBuilder sb= new StringBuilder();
		sb.append(destination+"/");
		for(int i=index-1; i<limit-1 ; i++)
		{
			sb.append(path.getName(i)+"/");
		}
		return Paths.get(sb.toString());
	}

	static void destination(String ...args) {
		try {
			
			if(args.length==0 || (args.length==1 && args[0].equals(""))) throw new IllegalArgumentException("Please Enter at least one File or Directory in command line to be copied");
			entries=args;
			}catch(IllegalArgumentException e)
		{
				System.err.println(e.getMessage());
				System.exit(1);
		}
		
		Path p= Paths.get(entries[entries.length-1]);
		
		if((!Files.exists(p) && !Files.isDirectory(p)) && entries.length==1)
		{
			try
			{
				throw new IllegalArgumentException("The directory or file entered in command line cannot be copied as it does not exist.");
			}catch(IllegalArgumentException e)
			{
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
		else if(!Files.exists(p) && !Files.isDirectory(p))
		{
			destination= entries[entries.length-1]; // checking if there is an empty destination folder at the end
		}
		else if(Files.exists(p) && Files.isDirectory(p) && entries.length!=1)
		{
			destination=entries[entries.length-1]; // checking if the last file is directory to be a destination folder
		}
		else destination= "dest"; // if no directory found at the last index then default directory is created
	}
	
	static int cuttingPath(String src) {
		//(Paths.get(entries[0]).getNameCount());
		for(int i=0; i<entries.length; i++)
		{
			if(src.equals(entries[i]))
			{
				index= (Paths.get(entries[i]).getNameCount());
			}
		}
		return index;
	}
	
	static int fileProcessing(Path target, Path path, String src)
	{
		int bites=0;
		try(FileOutputStream fos= new FileOutputStream(target+"/"+path.getFileName());
		FileInputStream fis= new FileInputStream(src);)
		{
			FileChannel fc= fis.getChannel();
			bites=(int) fc.size();
			ByteBuffer buffer=ByteBuffer.allocate(bites);
			fc.read(buffer);
			buffer.flip();
			byte[] bf= buffer.array();
			fos.write(bf);
			
		}catch(Exception e)
		{
			System.err.println("File does not exist");
		}
		return bites;
	}
	
}
