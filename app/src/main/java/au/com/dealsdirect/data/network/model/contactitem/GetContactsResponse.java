
package au.com.dealsdirect.data.network.model.contactitem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetContactsResponse {

    public Response getD() {
        return d;
    }

    private Response d;

    public class Response{

        @SerializedName("IsAuthenticated")
        @Expose
        public Boolean isAuthenticated;
        @SerializedName("List")
        @Expose
        public List<ContactList> list = null;
        @SerializedName("Result")
        @Expose
        public Boolean result;
        @SerializedName("Message")
        @Expose
        public String message;

        public Boolean getIsAuthenticated() {
            return isAuthenticated;
        }

        public void setIsAuthenticated(Boolean isAuthenticated) {
            this.isAuthenticated = isAuthenticated;
        }

        public List<ContactList> getList() {
            return list;
        }

        public void setList(List<ContactList> list) {
            this.list = list;
        }

        public Boolean getResult() {
            return result;
        }

        public void setResult(Boolean result) {
            this.result = result;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    public static class ContactList{
        @SerializedName("ContactNo")
        @Expose
        private Integer contactNo;
        @SerializedName("Subject")
        @Expose
        private String subject;
        @SerializedName("Comments")
        @Expose
        private Integer comments;
        @SerializedName("InvoiceNo")
        @Expose
        private Integer invoiceNo;
        @SerializedName("SaleName")
        @Expose
        private Object saleName;
        @SerializedName("LastComment")
        @Expose
        private String lastComment;
        @SerializedName("LastAnswer")
        @Expose
        private String lastAnswer;

        public Integer getContactNo() {
            return contactNo;
        }

        public void setContactNo(Integer contactNo) {
            this.contactNo = contactNo;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public Integer getComments() {
            return comments;
        }

        public void setComments(Integer comments) {
            this.comments = comments;
        }

        public Integer getInvoiceNo() {
            return invoiceNo;
        }

        public void setInvoiceNo(Integer invoiceNo) {
            this.invoiceNo = invoiceNo;
        }

        public Object getSaleName() {
            return saleName;
        }

        public void setSaleName(Object saleName) {
            this.saleName = saleName;
        }

        public String getLastComment() {
            return lastComment;
        }

        public void setLastComment(String lastComment) {
            this.lastComment = lastComment;
        }

        public String getLastAnswer() {
            return lastAnswer;
        }

        public void setLastAnswer(String lastAnswer) {
            this.lastAnswer = lastAnswer;
        }
    }

}
