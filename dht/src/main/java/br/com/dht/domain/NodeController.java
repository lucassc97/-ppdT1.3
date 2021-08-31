package br.com.dht.domain;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/node")
public class NodeController {

	@GetMapping(value = "/get")
	public String get() {
		return "Mari é linda";
	}
}