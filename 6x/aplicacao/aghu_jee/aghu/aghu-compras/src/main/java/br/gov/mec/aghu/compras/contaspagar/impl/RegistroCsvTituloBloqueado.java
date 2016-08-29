package br.gov.mec.aghu.compras.contaspagar.impl;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioBoletimOcorrencias;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateFormatUtil;

/**
 * <p>
 * Estrutuar como especificado em <a href=
 * "http://redmine.mec.gov.br/projects/aghu/repository/changes/documentos/Analise/Sprint36/LaranjaMecanica/2179/2179-GerarArquivoContasPeriodo.doc"
 * >http://redmine.mec.gov.br/projects/aghu/repository/changes/documentos/
 * Analise/Sprint36/LaranjaMecanica/2179/2179-GerarArquivoContasPeriodo.doc</a><br/>
 * Acessado em: <code>06.06.2011</code>
 * </p>
 * 
 * @author alejandro
 *
 */

public class RegistroCsvTituloBloqueado extends AbstractRegistroCsv {

	protected enum RegistroCsvTituloBloqueadoEnum implements CamposEnum {

		BO("bo", "BO"),
		SEQ("seq","Título"),
		IND_SITUACAO("indSituacao", "Situação"),
		DT_VENCIMENTO("dtVencimento", "Dt Vencto"),
		VALOR_NR("valorNr","Valor NR"),
		VALOR_TITULO("valorTitulo","Valor Título"),
		NR("nrsSeq", "NR"),
		NR_ORIGEM_NR("nrsSeqOrigemNf","NR Origem NF"),
		MOTIVO("motivo","Motivo"),
		FORNECEDOR("fornecedor","Fornecedor"),
		CNPJ_CPF("cnpj", "CNPJ/CPF"),
		ONDE_ESTA("", "Onde Está"),
		VALOR_BO("valorBo", "Valor BO"),
		CPF("cpf", null); // null -> Nao vai para os titulos nem dados
		
		
		private final String desc;
		private final String campo;

		private RegistroCsvTituloBloqueadoEnum(final String campo, final String desc) {
			this.campo = campo;
			this.desc = desc;
		}

		@Override
		public int getIndice() {
			return this.ordinal();
		}

		@Override
		public String getDescricao() {
			return this.desc;
		}

		@Override
		public String getCampo() {
			return this.campo;
		}
	}

	private Integer bo;
	private Integer seq;
	private DominioBoletimOcorrencias indSituacao;
	private Date dtVencimento;
	private Double valorNr;
	private Double valorTitulo;
	private BigDecimal valorBo;
	private Integer nrsSeq;
	private Integer nrsSeqOrigemNf;
	private String motivo;
	private String fornecedor;
	private Long cnpj;
	private Long cpf;
	
	public RegistroCsvTituloBloqueado( ) {
		super(NOME_TEMPLATE_LINHA_PONTO_VIRGULA);
	}

	protected Integer getBo() {
		return bo;
	}
	protected void setBo(Integer bo) {
		this.bo = bo;
	}
	protected Integer getSeq() {
		return seq;
	}
	protected void setSeq(Integer seq) {
		this.seq = seq;
	}
	protected DominioBoletimOcorrencias getIndSituacao() {
		return indSituacao;
	}
	protected void setIndSituacao(DominioBoletimOcorrencias indSituacao) {
		this.indSituacao = indSituacao;
	}
	protected Date getDtVencimento() {
		return dtVencimento;
	}
	protected void setDtVencimento(Date dtVencimento) {
		this.dtVencimento = dtVencimento;
	}
	public Double getValorNr() {
		return valorNr;
	}
	public void setValorNr(Double valorNr) {
		this.valorNr = valorNr;
	}
	public Double getValorTitulo() {
		return valorTitulo;
	}
	public void setValorTitulo(Double valorTitulo) {
		this.valorTitulo = valorTitulo;
	}
	protected Integer getNrsSeq() {
		return nrsSeq;
	}
	protected void setNrsSeq(Integer nrsSeq) {
		this.nrsSeq = nrsSeq;
	}
	protected Integer getNrsSeqOrigemNf() {
		return nrsSeqOrigemNf;
	}
	protected void setNrsSeqOrigemNf(Integer nrsSeqOrigemNf) {
		this.nrsSeqOrigemNf = nrsSeqOrigemNf;
	}
	protected String getMotivo() {
		return motivo;
	}
	protected void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	protected String getFornecedor() {
		return fornecedor;
	}
	protected void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}
	public BigDecimal getValorBo() {
		return valorBo;
	}
	public void setValorBo(BigDecimal valorBo) {
		this.valorBo = valorBo;
	}
	
	@Override
	public CamposEnum[] obterCampos() {
		// TODO Auto-generated method stub
		return RegistroCsvTituloBloqueadoEnum.values();
	}
	
	/**
	 *  Formatar resultados
	 */
	@Override
	protected Object obterRegistro(CamposEnum campo) {
		
		Object result = null;
		
		// Colocar tratamentos das colunas aqui
		if(campo != null && campo.equals(RegistroCsvTituloBloqueadoEnum.CNPJ_CPF)){
			if(this.cnpj != null){
				result = CoreUtil.formatarCNPJ(this.cnpj);
			} else if(this.cpf != null){
				result = CoreUtil.formataCPF(this.cnpj);
			} else {
				result = EMPTY_STR; // nao deveria ocorrer
			}			
		} else if (campo != null && campo.equals(RegistroCsvTituloBloqueadoEnum.CPF)) {
			result = null;
		} else if(campo != null && campo.equals(RegistroCsvTituloBloqueadoEnum.VALOR_TITULO)) {
			if(this.valorTitulo != null) {
				result = String.format("%.2f", this.valorTitulo);
			} 
		} else if(campo != null && campo.equals(RegistroCsvTituloBloqueadoEnum.VALOR_NR)) {
			if(this.valorNr != null) {
				result = String.format("%.2f", this.valorNr);
			}
		} else if(campo != null && campo.equals(RegistroCsvTituloBloqueadoEnum.VALOR_BO)) {
			if(this.valorBo != null) {
				result = String.format("%.2f", this.valorBo);
			}
		} else if(campo != null && campo.equals(RegistroCsvTituloBloqueadoEnum.DT_VENCIMENTO)){
			if(this.dtVencimento != null){
			   result = DateFormatUtil.fomataDiaMesAno(this.dtVencimento);
			}
		} else if(campo != null && campo.equals(RegistroCsvTituloBloqueadoEnum.IND_SITUACAO)){
			if(this.indSituacao != null){
				result = indSituacao.getDescricaoTituloBloqueado();
			}
		} else {
			result = super.obterRegistro(campo);
		}
		
		return result;
	}
}