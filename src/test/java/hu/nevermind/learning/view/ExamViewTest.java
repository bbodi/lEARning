package hu.nevermind.learning.view;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ExamViewTest {
	
	public ExamViewTest() {
	}
	
	@Before
	public void setUp() {
	}

	@Test
	public void testSomeMethod() {
		final String raw1 = "The container must call the ///@PostActivate andint/// method before ///@Remove/// method.";
		final String html1 = "The container must call the <span style='font-family: Courier;'><span style='color:purple;'>@</span>PostActivate andint</span> method before <span style='font-family: Courier;'><span style='color:purple;'>@</span>Remove</span> method.";
		assertEquals(html1, ExamView.syntaxHighlight(raw1));
		
		final String raw2 = "The container must ///123 42 ba12/// call";
		final String html2 = "The container must <span style='font-family: Courier;'><span style='color:red;'>123</span> <span style='color:red;'>42</span> ba12</span> call";
		assertEquals(html2, ExamView.syntaxHighlight(raw2));
		
		final String stringWithoutClosingPers = "Given:<br/>///Map&lt;String&nbsp;,&nbsp;List&lt;?&nbsp;extends&nbsp;CharSequence&gt;&gt;&nbsp;stateCitiesMap&nbsp;=&nbsp;new&nbsp;HashMap&lt;String,&nbsp;List&lt;?&nbsp;extends&nbsp;CharSequence&gt;&gt;();//<br/>/<br/><br/>Which&nbsp;of&nbsp;the&nbsp;following&nbsp;options&nbsp;correctly&nbsp;achieves&nbsp;the&nbsp;same&nbsp;declaration&nbsp;using&nbsp;type&nbsp;inferencing?";
		ExamView.syntaxHighlight(stringWithoutClosingPers); // no exception
	}
	
}
