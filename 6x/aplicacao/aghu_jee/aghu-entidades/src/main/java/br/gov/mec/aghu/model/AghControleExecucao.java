package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Range;

import br.gov.mec.aghu.dominio.DominioOperacaoAghControleExecucao;
import br.gov.mec.aghu.dominio.DominioStatusAghControleExecucao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "AGH_CONTROLE_EXECUCAO", schema = "AGH")
@SequenceGenerator(name = "aghSequenceControleExecucao", sequenceName = "AGH.AGH_CTE_SQ1", allocationSize = 1)
public class AghControleExecucao extends BaseEntitySeq<Integer> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3184468868131198312L;

	public AghControleExecucao() {}
	
	public AghControleExecucao(final AghSistemas sistema, final RapServidores servidorExecutor, final DominioOperacaoAghControleExecucao operacao) {
		this.sistema = sistema;
		this.servidorExecutor = servidorExecutor;
		this.operacao = operacao;
		this.dthrInicioProcessamento = new Date();
		this.status = DominioStatusAghControleExecucao.EXECUTANDO;
		this.percentualProcessado = Integer.valueOf(0);
	}
	
	
	/**
	 * Chave primária da base de dados, obtida via sequence.
	 */
	private Integer seq;

	/**
	 * controlhe de concorrência otimista JPA
	 */
	private Integer version;

	/**
	 * Percentual do arquivo que já foi processado. na inclusão este campo deve
	 * ter valor zero.
	 */
	private Integer percentualProcessado;

	/**
	 * sistema ao qual o arquivo está ligado.
	 */
	private AghSistemas sistema;


	/**
	 * Servidor que disparou a execução da operação
	 */
	private RapServidores servidorExecutor;


	/**
	 * Utilizado para descrever a operação sendo realizada possível ENUN
 	 */
	private DominioOperacaoAghControleExecucao operacao;

	/**
	 * Utilizado para informar o status da operação 1-Concluido, 2-Executando, 3-Concluido com Erro
 	 */
	private DominioStatusAghControleExecucao status;


	/**
	 * Utilizado para descrever o status da operação
 	 */
	private String descricaoStatus;


	/**
	 * Utilizado para descrever possíveis erros
 	 */
	private String erro;

	/**
	 * Data e hora início da execução da operação
	 */
	private Date dthrInicioProcessamento;
	
	/**
	 * Data e hora da execução mais recententemente da operação
	 */
	private Date dthrUltimoProcessamento;	

	/**
	 * Data e hora do fim da execução da operação
	 */
	private Date dthrFimProcessamento;
	
	
	public enum Fields {

		SEQ("seq"),
		PERCENTUAL_PROCESSADO("percentualProcessado"),
		SISTEMA("sistema"),
		SERVIDOR_EXECUTOR("servidorExecutor"),
		OPERACAO("operacao"),
		STATUS("status"),
		DESCRICAO_STATUS("descricaoStatus"),
		ERRO("erro"),
		DTHR_INICIO_PROCESSAMENTO("dthrInicioProcessamento"),
		DTHR_FIM_PROCESSAMENTO("dthrFimProcessamento");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}

	}
	
	
	
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghSequenceControleExecucao")
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer sequencial) {
		this.seq = sequencial;
	}
	
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	
	@Column(name = "PERCENTUAL_PROCESSADO", nullable = false)
	@Range(min = 0, max = 100, message="Percentual processado deve estar em 0 e 100.")
	public Integer getPercentualProcessado() {
		return percentualProcessado;
	}

	public void setPercentualProcessado(Integer percentualProcessado) {
		this.percentualProcessado = percentualProcessado;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SIS_SIGLA", nullable = false)
	public AghSistemas getSistema() {
		return sistema;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", nullable = false, referencedColumnName = "MATRICULA"),
	@JoinColumn(name = "SER_VIN_CODIGO", nullable = false, referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorExecutor() {
		return servidorExecutor;
	}

	@Column(name = "OPERACAO", nullable = false, length = 120)
	@Enumerated(EnumType.STRING)
	public DominioOperacaoAghControleExecucao getOperacao() {
		return operacao;
	}
	
	@Column(name = "STATUS", nullable = false, length = 18)
	@Enumerated(EnumType.STRING)
	public DominioStatusAghControleExecucao getStatus() {
		return status;
	}

	public void setStatus(DominioStatusAghControleExecucao status) {
		this.status = status;
		this.dthrUltimoProcessamento = new Date();
		this.dthrFimProcessamento = status != DominioStatusAghControleExecucao.EXECUTANDO ? new Date() : null;
	}

	@Column(name = "DESCRICAO_STATUS", length=120)
	public String getDescricaoStatus() {
		return descricaoStatus;
	}

	public void setDescricaoStatus(String descricaoStatus) {
		this.descricaoStatus = descricaoStatus;
		this.dthrUltimoProcessamento = new Date();
	}

	@Column(name = "ERRO", length=300)
	public String getErro() {
		return erro;
	}

	public void setErro(String erro) {
		this.erro = erro;
		this.dthrUltimoProcessamento = new Date();
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO_PROCESSAMENTO", nullable = false)
	public Date getDthrInicioProcessamento() {
		return dthrInicioProcessamento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM_PROCESSAMENTO")
	public Date getDthrFimProcessamento() {
		return dthrFimProcessamento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ULTIMO_PROCESSAMENTO", nullable = false)
	protected Date getDthrUltimoProcessamento() {
		return dthrUltimoProcessamento;
	}
	
	@PrePersist
	@SuppressWarnings("unused")
	private void validarDadosInclusao(){
		if (dthrInicioProcessamento == null) {
			dthrInicioProcessamento = new Date();
		}
		
		if (percentualProcessado == null) {
			percentualProcessado = Integer.valueOf(0);
		}
	}

	@PreUpdate
	@SuppressWarnings("unused")
	private void validarDadosAlteracao(){
		if (dthrUltimoProcessamento == null) {
			dthrUltimoProcessamento = new Date();
		}
		
		if (status != DominioStatusAghControleExecucao.EXECUTANDO) {
			dthrFimProcessamento = new Date();
		}
	}

	public void setSistema(AghSistemas sistema) {
		this.sistema = sistema;
	}

	public void setServidorExecutor(RapServidores servidorExecutor) {
		this.servidorExecutor = servidorExecutor;
	}

	public void setOperacao(DominioOperacaoAghControleExecucao operacao) {
		this.operacao = operacao;
	}

	public void setDthrInicioProcessamento(Date dthrInicioProcessamento) {
		this.dthrInicioProcessamento = dthrInicioProcessamento;
	}

	public void setDthrUltimoProcessamento(Date dthrUltimoProcessamento) {
		this.dthrUltimoProcessamento = dthrUltimoProcessamento;
	}

	public void setDthrFimProcessamento(Date dthrFimProcessamento) {
		this.dthrFimProcessamento = dthrFimProcessamento;
	}	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AghControleExecucao)) {
			return false;
		}
		AghControleExecucao other = (AghControleExecucao) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
