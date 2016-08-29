package br.gov.mec.aghu.patrimonio.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

public class DetalhamentoArquivosAnexosVO implements BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7209989665082512209L;
	private Long seqAA;
	private Integer tipoProcesso;
	private Integer tipoDocumento;
	private String tipoDocumentoOutrosAA;
	private String arquivoAA;
	private String descricaoAA;
	private Date criadoEmAA;
	private Date alteradoEmAA;
	private Integer matriculaAA;
	private Short vinCodigoAA;
	private Integer matriculaAlteracaoAA;
	public Integer getTipoProcesso() {
		return tipoProcesso;
	}

 
	private Short vinCodigoAlteracaoAA;
	private Integer ipfPfrLctNumero;
	private Long numeroDfe;
	private Integer numeroSlc;
	private Integer nrpSeq;
	private Integer nroItem;
	private Integer codigoMat;
	private Integer eslSeq;
	private Long seqNt;
	private String descricaoNt;
	private Long numeroBemBp;
	private String nome;
	private String nomeAlteracao;
	private Integer aceiteTecnico;
	private String horaInc;
	private String horaAlt;
	
	
	


	public enum Fields {
		SEQ("seqAA"),
		TIPO_PROCESSO("tipoProcesso"),
		TIPO_DOCUMENTO("tipoDocumento"),
		DESCRICAO_OUTROS("tipoDocumentoOutrosAA"),
		ARQUIVO("arquivoAA"),
		DESCRICAO("descricaoAA"),
		DT_INCLUSAO("criadoEmAA"),
		SER_MATRICULA("matriculaAA"),
		SER_VIN_CODIGO("vinCodigoAA"),
		DT_ALTERACAO("alteradoEmAA"),
		SER_MATRICULA_ALTERACAO("matriculaAlteracaoAA"),
		SER_VIN_CODIGO_ALTERACAO("vinCodigoAlteracaoAA"),
		NRO_AF("ipfPfrLctNumero"),
		NOTA_FISCAL("numeroDfe"),
		SC("numeroSlc"),
		RECEB("nrpSeq"),
		ITEM("nroItem"),
		COD_MATERIAL("codigoMat"),
		ESL("eslSeq"),
		SEQ_NOTA_TECNICA("seqNt"),
		DESC_NOTA_TECNICA("descricaoNt"),
		PATRIMONIO("numeroBemBp"),
		NOME_INCLUSAO("nome"),
		NOME_ALTERACAO("nomeAlteracao"),
		ACEITE_TECNICO("aceiteTecnico"),
		HORA_INC("horaInc"),
		HORA_ALT("horaAlt");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeAlteracao() {
		return nomeAlteracao;
	}

	public void setNomeAlteracao(String nomeAlteracao) {
		this.nomeAlteracao = nomeAlteracao;
	}
	
	public Long getSeqAA() {
		return seqAA;
	}

	public void setSeqAA(Long seqAA) {
		this.seqAA = seqAA;
	}

	public String getTipoDocumentoOutrosAA() {
		return tipoDocumentoOutrosAA;
	}

	public void setTipoDocumentoOutrosAA(String tipoDocumentoOutrosAA) {
		this.tipoDocumentoOutrosAA = tipoDocumentoOutrosAA;
	}

	public String getArquivoAA() {
		return arquivoAA;
	}

	public void setArquivoAA(String arquivoAA) {
		this.arquivoAA = arquivoAA;
	}

	public String getDescricaoAA() {
		return descricaoAA;
	}

	public void setDescricaoAA(String descricaoAA) {
		this.descricaoAA = descricaoAA;
	}

	public Date getCriadoEmAA() {
		return criadoEmAA;
	}

	public void setCriadoEmAA(Date criadoEmAA) {
		this.criadoEmAA = criadoEmAA;
	}

	public Date getAlteradoEmAA() {
		return alteradoEmAA;
	}

	public void setAlteradoEmAA(Date alteradoEmAA) {
		this.alteradoEmAA = alteradoEmAA;
	}

	public Integer getMatriculaAA() {
		return matriculaAA;
	}

	public void setMatriculaAA(Integer matriculaAA) {
		this.matriculaAA = matriculaAA;
	}

	public Short getVinCodigoAA() {
		return vinCodigoAA;
	}

	public void setVinCodigoAA(Short vinCodigoAA) {
		this.vinCodigoAA = vinCodigoAA;
	}

	public Integer getMatriculaAlteracaoAA() {
		return matriculaAlteracaoAA;
	}

	public void setMatriculaAlteracaoAA(Integer matriculaAlteracaoAA) {
		this.matriculaAlteracaoAA = matriculaAlteracaoAA;
	}

	public Short getVinCodigoAlteracaoAA() {
		return vinCodigoAlteracaoAA;
	}

	public void setVinCodigoAlteracaoAA(Short vinCodigoAlteracaoAA) {
		this.vinCodigoAlteracaoAA = vinCodigoAlteracaoAA;
	}

	public Integer getIpfPfrLctNumero() {
		return ipfPfrLctNumero;
	}

	public void setIpfPfrLctNumero(Integer ipfPfrLctNumero) {
		this.ipfPfrLctNumero = ipfPfrLctNumero;
	}

	public Long getNumeroDfe() {
		return numeroDfe;
	}

	public void setNumeroDfe(Long numeroDfe) {
		this.numeroDfe = numeroDfe;
	}

	public Integer getNumeroSlc() {
		return numeroSlc;
	}

	public void setNumeroSlc(Integer numeroSlc) {
		this.numeroSlc = numeroSlc;
	}

	public Integer getNrpSeq() {
		return nrpSeq;
	}

	public void setNrpSeq(Integer nrpSeq) {
		this.nrpSeq = nrpSeq;
	}

	public Integer getNroItem() {
		return nroItem;
	}

	public void setNroItem(Integer nroItem) {
		this.nroItem = nroItem;
	}

	public Integer getCodigoMat() {
		return codigoMat;
	}

	public void setCodigoMat(Integer codigoMat) {
		this.codigoMat = codigoMat;
	}

	public Integer getEslSeq() {
		return eslSeq;
	}

	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}

	public Long getSeqNt() {
		return seqNt;
	}

	public void setSeqNt(Long seqNt) {
		this.seqNt = seqNt;
	}

	public String getDescricaoNt() {
		return descricaoNt;
	}

	public void setDescricaoNt(String descricaoNt) {
		this.descricaoNt = descricaoNt;
	}

	public Long getNumeroBemBp() {
		return numeroBemBp;
	}

	public void setNumeroBemBp(Long numeroBemBp) {
		this.numeroBemBp = numeroBemBp;
	}

	public String obterDataFormatadaCriacao(){
		return DateUtil.obterDataFormatada(this.getCriadoEmAA(),DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS);
	}
	public String obterDataFormatadaAlteracao(){
		return DateUtil.obterDataFormatada(this.getAlteradoEmAA(),DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS);
	}

	public Integer getAceiteTecnico() {
		return aceiteTecnico;
	}

	public void setAceiteTecnico(Integer aceiteTecnico) {
		this.aceiteTecnico = aceiteTecnico;
	}

	public String getHoraInc() {
		return horaInc;
	}

	public void setHoraInc(String horaInc) {
		this.horaInc = horaInc;
	}

	public String getHoraAlt() {
		return horaAlt;
	}

	public void setHoraAlt(String horaAlt) {
		this.horaAlt = horaAlt;
	}
	
	public void setTipoProcesso(Integer tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}

	public Integer getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(Integer tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

}
