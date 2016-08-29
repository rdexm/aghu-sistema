package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioSituacaoDispensacaoMdtoImagens implements Dominio {

	SOLICITADO_SELECIONADO	(Boolean.FALSE, "silk-medicamentos-solicitar-atual",DominioSituacaoDispensacaoMdto.S, "Solicitado"),
	SOLICITADO_DESABILITADO	(Boolean.FALSE, "silk-medicamentos-solicitar-off", 	DominioSituacaoDispensacaoMdto.S, "Solicitar Inativo"),
	SOLICITADO_HABILITADO	(Boolean.TRUE, 	"silk-medicamentos-solicitar", 		DominioSituacaoDispensacaoMdto.S, "Solicitar"),
	
	TRIADO_SELECIONADO		(Boolean.FALSE, "silk-medicamentos-triar-atual", 	DominioSituacaoDispensacaoMdto.T, "Triado"),
	TRIADO_DESABILITADO		(Boolean.FALSE, "silk-medicamentos-triar-off", 		DominioSituacaoDispensacaoMdto.T, "Triar Inativo"),
	TRIADO_HABILITADO		(Boolean.TRUE, 	"silk-medicamentos-triar", 			DominioSituacaoDispensacaoMdto.T, "Triar"),
	
	DISPENSADO_SELECIONADO	(Boolean.FALSE, "silk-medicamentos-dispensar-atual",DominioSituacaoDispensacaoMdto.D, "Dispensado"),
	DISPENSADO_DESABILITADO	(Boolean.FALSE, "silk-medicamentos-dispensar-off", 	DominioSituacaoDispensacaoMdto.D, "Dispensar Inativo"),
	DISPENSADO_HABILITADO	(Boolean.TRUE, 	"silk-medicamentos-dispensar", 		DominioSituacaoDispensacaoMdto.D, "Dispensar"),
	
	CONFERIDO_SELECIONADO	(Boolean.FALSE, "silk-medicamentos-conferir-atual", DominioSituacaoDispensacaoMdto.C, "Conferido"),
	CONFERIDO_DESABILITADO	(Boolean.FALSE, "silk-medicamentos-conferir-off", 	DominioSituacaoDispensacaoMdto.C, "Conferir Inativo"),
	CONFERIDO_HABILITADO	(Boolean.TRUE, 	"silk-medicamentos-conferir", 		DominioSituacaoDispensacaoMdto.C, "Conferir"),
	
	ENVIADO_SELECIONADO		(Boolean.FALSE, "silk-medicamentos-enviar-atual", 	DominioSituacaoDispensacaoMdto.E, "Enviado"),
	ENVIADO_DESABILITADO	(Boolean.FALSE, "silk-medicamentos-enviar-off", 	DominioSituacaoDispensacaoMdto.E, "Enviar Inativo"),
	ENVIADO_HABILITADO		(Boolean.TRUE, 	"silk-medicamentos-enviar", 		DominioSituacaoDispensacaoMdto.E, "Enviar")
	;
	
	private DominioSituacaoDispensacaoMdtoImagens(Boolean clicavel, String endImagem, DominioSituacaoDispensacaoMdto indSituacaoAssociado, String label) {
		this.clicavel = clicavel;
		this.endImagem = endImagem;
		this.indSituacaoAssociado = indSituacaoAssociado;
		this.label = label;
	}
	
	private Boolean clicavel;
	private String endImagem;
	private DominioSituacaoDispensacaoMdto indSituacaoAssociado;
	private String label;
	
	public Boolean getClicavel() {
		return clicavel;
	}
	
	public String getEndImagem() {
		return endImagem;
	}
	
	public DominioSituacaoDispensacaoMdto getIndSituacaoAssociado() {
		return indSituacaoAssociado;
	}
	
	public String getLabel() {
		return label;
	}
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	@Override
	public String getDescricao() {
		return this.name();
	}

}
