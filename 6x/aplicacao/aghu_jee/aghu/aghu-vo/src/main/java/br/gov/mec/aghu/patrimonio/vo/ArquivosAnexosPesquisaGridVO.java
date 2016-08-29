package br.gov.mec.aghu.patrimonio.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;


public class ArquivosAnexosPesquisaGridVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7154998779949909569L;
			
			
			private Integer nroAf;
			private Integer sc;
			private Long notaFiscal;
			private Date dtUltAltera;
			private Long nroBem;
			private Long aaSeq;
			private Integer tipoDocumento;
			private Integer tipoProcesso;
			private String arquivo;
			private String descricao;
			private Date dtCriadoEm;
			private String horaInc;
			private String horaAlt;



			private String nome;
			private Integer matricula;
			private Short vinCodigo;
			private String nomeAlteracao;
			private Integer matriculaAlteracao;
			private Short vinCodigoAlteracao;
			private Integer fornNumero;
			private Byte[] anexo;
			
			public Byte[] getAnexo() {
				return anexo;
			}
			public void setAnexo(Byte[] anexo) {
				this.anexo = anexo;
			}
			public Integer getNroAf() {
				return nroAf;
			}
			public void setNroAf(Integer nroAf) {
				this.nroAf = nroAf;
			}
			public Integer getSc() {
				return sc;
			}
			public void setSc(Integer sc) {
				this.sc = sc;
			}
			public Long getNotaFiscal() {
				return notaFiscal;
			}
			public void setNotaFiscal(Long notaFiscal) {
				this.notaFiscal = notaFiscal;
			}
			
			public Integer getTipoDocumento() {
				return tipoDocumento;
			}
			public void setTipoDocumento(Integer tipoDocumento) {
				this.tipoDocumento = tipoDocumento;
			}
			
			public Integer getTipoProcesso() {
				return tipoProcesso;
			}
			public void setTipoProcesso(Integer tipoProcesso) {
				this.tipoProcesso = tipoProcesso;
			}
			
			public String getArquivo() {
				return arquivo;
			}
			public void setArquivo(String arquivo) {
				this.arquivo = arquivo;
			}
			
			public Long getNroBem() {
				return nroBem;
			}
			public void setNroBem(Long nroBem) {
				this.nroBem = nroBem;
			}
			public Long getAaSeq() {
				return aaSeq;
			}
			public String obterNroBemFormatado() {
				if (nroBem != null){
					return nroBem.toString();					
				}
				return null;
			}
			public void setAaSeq(Long aaSeq) {
				this.aaSeq = aaSeq;
			}
			public Date getDtUltAltera() {
				return dtUltAltera;
			}
			public void setDtUltAltera(Date dtUltAltera) {
				this.dtUltAltera = dtUltAltera;
			}
			public String getDescricao() {
				return descricao;
			}
			public void setDescricao(String descricao) {
				this.descricao = descricao;
			}
			public Date getDtCriadoEm() {
				return dtCriadoEm;
			}
			public void setDtCriadoEm(Date dtCriadoEm) {
				this.dtCriadoEm = dtCriadoEm;
			}
			public Integer getMatricula() {
				return matricula;
			}
			public void setMatricula(Integer matricula) {
				this.matricula = matricula;
			}
			public Short getVinCodigo() {
				return vinCodigo;
			}
			public void setVinCodigo(Short vinCodigo) {
				this.vinCodigo = vinCodigo;
			}
			public Integer getMatriculaAlteracao() {
				return matriculaAlteracao;
			}
			public void setMatriculaAlteracao(Integer matriculaAlteracao) {
				this.matriculaAlteracao = matriculaAlteracao;
			}
			public Short getVinCodigoAlteracao() {
				return vinCodigoAlteracao;
			}
			public void setVinCodigoAlteracao(Short vinCodigoAlteracao) {
				this.vinCodigoAlteracao = vinCodigoAlteracao;
			}
			public Integer getFornNumero() {
				return fornNumero;
			}
			public void setFornNumero(Integer fornNumero) {
				this.fornNumero = fornNumero;
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

			public enum Fields {
				AA_SEQ("aaSeq"),
				NRO_AF("nroAf"),
				DFE_SEQ("notaFiscal"),
				SC("sc"),
				DT_ULT_ALTERA("dtUltAltera"),
				NRO_BEM("nroBem"),
				TIPO_DOCUMENTO("tipoDocumento"),
				TIPO_PROCESSO("tipoProcesso"),
				ARQUIVO("arquivo"),
				DESCRICAO("descricao"),
				CRIADO_EM("dtCriadoEm"),
				NOME("nome"),
				NOME_ALTERACAO("nomeAlteracao"),
				MATRICULA("matricula"),
				MATRICULA_ALTERACAO("matriculaAlteracao"),
				VIN_CODIGO("vinCodigo"),
				VIN_CODIGO_ALTERACAO("vinCodigoAlteracao"),
				FORN_NUMERO("fornNumero"),
				ANEXO("anexo"),
				HORA_INC("horaInc"),
				HORA_ALT("horaAlt")
				;
				private String fields;

				private Fields(String fields) {
					this.fields = fields;
				}

				@Override
				public String toString() {
					return fields;
				}
			}
}
