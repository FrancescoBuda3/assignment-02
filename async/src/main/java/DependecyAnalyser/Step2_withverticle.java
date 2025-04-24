package DependecyAnalyser;

import io.vertx.core.AsyncResult;
import io.vertx.core.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

/**
 * 
 * Simple Verticle - i.e. an event-loop in Vert.x
 * 
 */
class MyReactiveAgent extends AbstractVerticle {
	
	public void start() {
		log("1 - doing the async call...");
		
		FileSystem fs = getVertx().fileSystem();    		
		
		Future<Buffer> f1 = fs.readFile("README.md");

		f1.onComplete((AsyncResult<Buffer> res) -> {
			log("4 - README \n" + res.result().toString().substring(0,160));
		});
	
		log("2 - doing the second async call...");

		fs
		.readFile("async/pom.xml")
		.onComplete((AsyncResult<Buffer> res) -> {
			log("4 - POM \n" + res.result().toString().substring(0,160));
		});
		
		log("3 - done");
	}

	private void log(String msg) {
		System.out.println("[ " + System.currentTimeMillis() + " ][ " + Thread.currentThread() + " ] " + msg);
	}
}

public class Step2_withverticle {

	public static void main(String[] args) {
		Vertx  vertx = Vertx.vertx();
		vertx.deployVerticle(new MyReactiveAgent());
	}
}