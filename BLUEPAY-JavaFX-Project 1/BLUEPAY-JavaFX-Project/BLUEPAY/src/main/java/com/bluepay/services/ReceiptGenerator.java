package com.bluepay.services;
import com.bluepay.models.Transaction;
import org.apache.pdfbox.pdmodel.*;import org.apache.pdfbox.pdmodel.common.PDRectangle;import org.apache.pdfbox.pdmodel.font.Standard14Fonts;import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.nio.file.Path;import java.io.IOException;
public class ReceiptGenerator {
 public Path generate(Transaction t,Path path)throws IOException{
  try(PDDocument doc=new PDDocument()){
   PDPage page=new PDPage(PDRectangle.A6);doc.addPage(page);
   try(PDPageContentStream cs=new PDPageContentStream(doc,page)){
    PDType1Font bold=new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),regular=new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    float y=page.getMediaBox().getHeight()-45;
    line(cs,bold,20,16,y,"BLUEPAY");y-=30;line(cs,bold,18,16,y,"PAYMENT RECEIPT");y-=30;
    line(cs,regular,11,16,y,"Status: "+t.status());y-=20;line(cs,bold,22,16,y,"INR "+String.format("%.2f",t.amount()));y-=30;
    String[] rows={"Recipient: "+t.recipient(),"UPI ID: "+t.upiId(),"Transaction ID: "+t.txnId(),"Date: "+t.createdAt(),"Category: "+t.category(),"Note: "+(t.description()==null?"":t.description()),"","Educational simulation only. No real money moved."};
    for(String row:rows){line(cs,regular,9,16,y,row);y-=17;}
   }doc.save(path.toFile());
  }return path;
 }
 private void line(PDPageContentStream cs,PDType1Font f,int size,float x,float y,String s)throws IOException{cs.beginText();cs.setFont(f,size);cs.newLineAtOffset(x,y);cs.showText(s.replaceAll("[^\\x20-\\x7E]"," "));cs.endText();}
}