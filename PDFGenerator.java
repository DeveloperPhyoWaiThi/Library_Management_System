package com.sample.util;




import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.stereotype.Component;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sample.entity.Author;
import com.sample.entity.Transaction;

@Component
public class PDFGenerator {
	
	public void generateItinerary(Transaction t,String filePath) throws IOException {
		
		Document d = new Document();
		
		try {
			PdfWriter.getInstance(d, new FileOutputStream(filePath));
			d.open();
			PdfPTable table = generateTable(t);
            
            Paragraph paragraph = new Paragraph();
            paragraph.add(table);
            paragraph.setAlignment(Element.ALIGN_CENTER); // Center the table
            
            d.add(paragraph);
            Image img = Image.getInstance("src/main/resources/static/images/stamp.png");
			img.scaleToFit(150, 150); 
			img.setAlignment(Element.ALIGN_RIGHT);
			d.add(img);
			d.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public PdfPTable generateTable(Transaction t) {
		
		PdfPTable p=new PdfPTable(2);
		
		PdfPCell cell;
		
		
	    cell = new PdfPCell(new Phrase("Borrow Circulation Voucher"));
	    cell.setColspan(2);
	    cell.setBorder(Rectangle.NO_BORDER); 
	    cell.setHorizontalAlignment(Element.ALIGN_CENTER); 
	    p.addCell(cell);
	    
	    cell = new PdfPCell(new Phrase("---------------------------------"));
	    cell.setColspan(2);
	    cell.setBorder(Rectangle.NO_BORDER); 
	    cell.setHorizontalAlignment(Element.ALIGN_CENTER); 
	    p.addCell(cell);
	    
	    
	    
	    // Add cells for each transaction detail
	    addCell(p, "Transaction ID :", t.getTransactionId().toString());
	    addCell(p, "User Name :", t.getUser().getName());
	    addCell(p, "User Email :", t.getUser().getEmail());
	    addCell(p, "Book Title :", t.getBook().getTitle());
	    
	    int count=0;
	    for (Author author : t.getBook().getAuthors()) {
	    	if (count<=0) {
	    		addCell(p, "Authors", author.getAuthorName());
	    		count +=1;
	    	}
	    	else {
	    		addCell(p, "      ", author.getAuthorName());
	    	}
             
            
        }
	    addCell(p, "Book Category :", t.getBook().getCategory().getCategoryName());
	    addCell(p, "Borrow Date :", t.getBorrowDate().toString());
	    addCell(p, "Return Date :", t.getDueDate().toString());
	    
	    cell = new PdfPCell(new Phrase("Notice"));
	    cell.setColspan(2);
	    cell.setBorder(Rectangle.NO_BORDER); // Remove border 
	    cell.setPadding(10f);
	    p.addCell(cell);
	    
	    Font italicFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC);
	    
	    cell = new PdfPCell(new Phrase("1. Please handle the book with care. Avoid damaging the cover and pages.",italicFont));
	    cell.setColspan(2);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setPadding(10f);
	    p.addCell(cell);
	    
	    cell = new PdfPCell(new Phrase("2. Please return the book by the due date to avoid late fees.",italicFont));
	    cell.setColspan(2);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setPadding(10f);
	    p.addCell(cell);
	    
	    cell = new PdfPCell(new Phrase("3. Inspect the book before returning. Report any damages to the librarian.",italicFont));
	    cell.setColspan(2);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setPadding(10f);
	    p.addCell(cell);
	    
	    cell = new PdfPCell(new Phrase("",italicFont));
	    cell.setColspan(2);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setPadding(10f);
	    p.addCell(cell);
	   
	    

	    // Set table border to no border
	    p.getDefaultCell().setBorder(Rectangle.NO_BORDER);

	    return p;
	}

	private void addCell(PdfPTable table, String header, String value) {
	    PdfPCell headerCell = new PdfPCell(new Phrase(header));
	    headerCell.setBorder(Rectangle.NO_BORDER);
	    headerCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
	    headerCell.setPadding(10f);
	    
	    table.addCell(headerCell);
	    
	    PdfPCell valueCell = new PdfPCell(new Phrase(value));
	    valueCell.setBorder(Rectangle.NO_BORDER);
	    valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    valueCell.setPadding(10f);
	    table.addCell(valueCell);
	}
	
	
	

}