package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioTipoCadastroItemContrato;
import br.gov.mec.aghu.dominio.DominioTipoConsultaAssinarAF;

public class FiltroPesquisaAssinarAFVO implements Serializable {
	
	private static final long serialVersionUID = 5591167170080346016L;
	private Integer numeroAf;
	private Short numeroComplemento;
	private DominioTipoConsultaAssinarAF tipoConsulta;
	private Boolean indContrato;
	private Integer numeroContrato; // NRO_CONTRATO
	private Integer numeroFornecedor; // PFR_FRN_NUMERO
	private Integer matriculaGestor; // SER_MATRICULA_GESTOR
	private Short vinCodigoGestor; // SER_VIN_CODIGO_GESTOR
	private DominioTipoCadastroItemContrato tipoCompra;
	private Integer codigoGrupoMaterial;
	private Integer codigoGrupoServico;
	private String codigoModalidadeCompra;
	private Integer seqVerbaGestao;
	private Integer codigoGrupoNaturezaDespesa;
	private Byte codigoNaturezaDespesa;
	
//	private DominioSituacaoAutorizacaoFornecimento situacaoAf;
//	private DominioAndamentoAutorizacaoFornecimento andamentoAf;
//	private ScoFornecedor fornecedor;
//	private ScoModalidadeLicitacao modalidadeCompra;
//	private RapServidores servidorGestor;
//	private DominioTipoFiltroAutorizacaoFornecimento tipoFiltroAf;
//	private Integer codigoFiltroAf;
	
//	private Date dataInicioContrato;
//	private Date dataFimContrato;
	
	
	
	public FiltroPesquisaAssinarAFVO() {
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numeroAf == null) ? 0 : numeroAf.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		FiltroPesquisaAssinarAFVO other = (FiltroPesquisaAssinarAFVO) obj;
		if (numeroAf == null) {
			if (other.getNumeroAf() != null){
				return false;
			}
		} else if (!numeroAf.equals(other.getNumeroAf())){
			return false;
		}
		return true;
	}

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getNumeroComplemento() {
		return numeroComplemento;
	}

	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}

	public DominioTipoConsultaAssinarAF getTipoConsulta() {
		return tipoConsulta;
	}

	public void setTipoConsulta(DominioTipoConsultaAssinarAF tipoConsulta) {
		this.tipoConsulta = tipoConsulta;
	}

	public Boolean getIndContrato() {
		return indContrato;
	}

	public void setIndContrato(Boolean indContrato) {
		this.indContrato = indContrato;
	}

	public Integer getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(Integer numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public Integer getMatriculaGestor() {
		return matriculaGestor;
	}

	public void setMatriculaGestor(Integer matriculaGestor) {
		this.matriculaGestor = matriculaGestor;
	}

	public Short getVinCodigoGestor() {
		return vinCodigoGestor;
	}

	public void setVinCodigoGestor(Short vinCodigoGestor) {
		this.vinCodigoGestor = vinCodigoGestor;
	}

	public DominioTipoCadastroItemContrato getTipoCompra() {
		return tipoCompra;
	}

	public void setTipoCompra(DominioTipoCadastroItemContrato tipoCompra) {
		this.tipoCompra = tipoCompra;
	}

	public Integer getCodigoGrupoMaterial() {
		return codigoGrupoMaterial;
	}

	public void setCodigoGrupoMaterial(Integer codigoGrupoMaterial) {
		this.codigoGrupoMaterial = codigoGrupoMaterial;
	}

	public Integer getCodigoGrupoServico() {
		return codigoGrupoServico;
	}

	public void setCodigoGrupoServico(Integer codigoGrupoServico) {
		this.codigoGrupoServico = codigoGrupoServico;
	}

	public String getCodigoModalidadeCompra() {
		return codigoModalidadeCompra;
	}

	public void setCodigoModalidadeCompra(String codigoModalidadeCompra) {
		this.codigoModalidadeCompra = codigoModalidadeCompra;
	}

	public Integer getSeqVerbaGestao() {
		return seqVerbaGestao;
	}

	public void setSeqVerbaGestao(Integer seqVerbaGestao) {
		this.seqVerbaGestao = seqVerbaGestao;
	}

	public Integer getCodigoGrupoNaturezaDespesa() {
		return codigoGrupoNaturezaDespesa;
	}

	public void setCodigoGrupoNaturezaDespesa(Integer codigoGrupoNaturezaDespesa) {
		this.codigoGrupoNaturezaDespesa = codigoGrupoNaturezaDespesa;
	}

	public Byte getCodigoNaturezaDespesa() {
		return codigoNaturezaDespesa;
	}

	public void setCodigoNaturezaDespesa(Byte codigoNaturezaDespesa) {
		this.codigoNaturezaDespesa = codigoNaturezaDespesa;
	}

	
}
