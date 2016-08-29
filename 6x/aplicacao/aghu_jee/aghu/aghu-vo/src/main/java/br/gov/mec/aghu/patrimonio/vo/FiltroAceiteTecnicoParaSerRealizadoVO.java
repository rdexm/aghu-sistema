package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;

import br.gov.mec.aghu.compras.contaspagar.vo.FornecedorVO;
import br.gov.mec.aghu.dominio.DominioStatusAceiteTecnico;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;

public class FiltroAceiteTecnicoParaSerRealizadoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 309947833512315060L;
	
	private Integer numeroRecebimento;
	private Integer numeroAf;
	private Short complementoAf;
	private Integer nroSolicitacaoCompra;
	private Long notaFiscal;
	private ScoMaterial material;
	private RapServidores responsavelTecnico;
	private PtmAreaTecAvaliacao areaTecnicaAvaliacao;
	private DominioStatusAceiteTecnico status;
	private FornecedorVO fornecedor;
	
	public Integer getNumeroRecebimento() {
		return numeroRecebimento;
	}
	
	public void setNumeroRecebimento(Integer numeroRecebimento) {
		this.numeroRecebimento = numeroRecebimento;
	}
	
	public Integer getNumeroAf() {
		return numeroAf;
	}
	
	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}
	
	public Short getComplementoAf() {
		return complementoAf;
	}
	
	public void setComplementoAf(Short complementoAf) {
		this.complementoAf = complementoAf;
	}
	
	public Integer getNroSolicitacaoCompra() {
		return nroSolicitacaoCompra;
	}

	public void setNroSolicitacaoCompra(Integer nroSolicitacaoCompra) {
		this.nroSolicitacaoCompra = nroSolicitacaoCompra;
	}
	
	public Long getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(Long notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	public ScoMaterial getMaterial() {
		return material;
	}
	
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	
	public RapServidores getResponsavelTecnico() {
		return responsavelTecnico;
	}
	
	public void setResponsavelTecnico(RapServidores responsavelTecnico) {
		this.responsavelTecnico = responsavelTecnico;
	}
	
	public PtmAreaTecAvaliacao getAreaTecnicaAvaliacao() {
		return areaTecnicaAvaliacao;
	}
	
	public void setAreaTecnicaAvaliacao(PtmAreaTecAvaliacao areaTecnicaAvaliacao) {
		this.areaTecnicaAvaliacao = areaTecnicaAvaliacao;
	}
	
	public DominioStatusAceiteTecnico getStatus() {
		return status;
	}
	
	public void setStatus(DominioStatusAceiteTecnico status) {
		this.status = status;
	}

	public FornecedorVO getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(FornecedorVO fornecedor) {
		this.fornecedor = fornecedor;
	}

}
