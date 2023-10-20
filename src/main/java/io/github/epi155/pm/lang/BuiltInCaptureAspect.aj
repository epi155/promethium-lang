package io.github.epi155.pm.lang;

public aspect BuiltInCaptureAspect {
	pointcut noCapture() : execution(@NoBuiltInCapture * *(..));

	pointcut returnSome() : execution(Some *.*(..));
	pointcut returnNone() : execution(None *.*(..));
	pointcut returnHope() : execution(Hope *.*(..));
	pointcut returnNope() : execution(Nope *.*(..));
	pointcut returnSearch() : execution(SearchResult *.*(..));

	Some around() : returnSome() && !noCapture() {
		try {
			return proceed();
		} catch (Exception e) {
			return Some.capture(e);
		}
	}

	None around() : returnNone() && !noCapture() {
		try {
			return proceed();
		} catch (Exception e) {
			return None.capture(e);
		}
	}

	Hope around() : returnHope() && !noCapture() {
		try {
			return proceed();
		} catch (Exception e) {
			return Hope.capture(e);
		}
	}

	Nope around() : returnNope() && !noCapture() {
		try {
			return proceed();
		} catch (Exception e) {
			return Nope.capture(e);
		}
	}

	SearchResult around() : returnSearch() && !noCapture() {
		try {
			return proceed();
		} catch (Exception e) {
			return SearchResult.capture(e);
		}
	}
}