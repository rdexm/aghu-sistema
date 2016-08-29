package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioDiasMes;
import br.gov.mec.aghu.dominio.DominioFatoGerador;
import br.gov.mec.aghu.dominio.DominioMesVencimento;
import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.dominio.DominioVencimentoDiaNaoUtil;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * 
 * @author Juliano Sena
 */

public class FcpCalendarioVencimentoTributosVO implements BaseBean {

	private static final long serialVersionUID = 2893103861851346605L;

	private Integer seq; //Número Calendário
    private Date inicioVigencia; //Início Vigência
    private DominioTipoTributo tipoTributo; //Tipo de Tributo
    private DominioFatoGerador fatoGerador; //Fato Gerador 
    private DominioVencimentoDiaNaoUtil vencimentoDiaNaoUtil; //Vencimento em Dia Não Útil 
    private DominioDiasMes inicioPeriodo; //Período de Apuração – Inicial 
    private DominioDiasMes fimPeriodo; //Período de Apuração – Final 
    private DominioDiasMes diaVencimento; //Dia Vencimento 
    private DominioMesVencimento mesVencimento; //Mês Vencimento
    private String observacao; //Observação

    /**
     * Campos usados para exibição de dados formatados
     * na listagem de resultados na página pesquisarCalendarioVencimentosTributos.xhtml
     * 
     * @author julianosena
     *
     */
    public class FormattedFields {
		public String tipoTributo = "";
    	public String periodoApuracao = "";
    	public String vencimento = "";
    	public String inicioVigencia = "";

    	public String getTipoTributo() {
			return tipoTributo;
		}

		public void setTipoTributo(String tipoTributo) {
			this.tipoTributo = tipoTributo;
		}

		public String getPeriodoApuracao() {
			return periodoApuracao;
		}

		public void setPeriodoApuracao(String periodoApuracao) {
			this.periodoApuracao = periodoApuracao;
		}

		public String getVencimento() {
			return vencimento;
		}

		public void setVencimento(String vencimento) {
			this.vencimento = vencimento;
		}

		public String getInicioVigencia() {
			return inicioVigencia;
		}

		public void setInicioVigencia(String inicioVigencia) {
			this.inicioVigencia = inicioVigencia;
		}
    }

    private FormattedFields formattedFields;
    
    public FcpCalendarioVencimentoTributosVO() {
    	this.formattedFields = new FormattedFields();
    }

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Date getInicioVigencia() {
		return inicioVigencia;
	}

	public void setInicioVigencia(Date inicioVigencia) {
		this.inicioVigencia = inicioVigencia;
	}

	public DominioTipoTributo getTipoTributo() {
		return tipoTributo;
	}

	public void setTipoTributo(DominioTipoTributo tipoTributo) {
		this.tipoTributo = tipoTributo;
	}

	public DominioFatoGerador getFatoGerador() {
		return fatoGerador;
	}

	public void setFatoGerador(DominioFatoGerador fatoGerador) {
		this.fatoGerador = fatoGerador;
	}

	public DominioVencimentoDiaNaoUtil getVencimentoDiaNaoUtil() {
		return vencimentoDiaNaoUtil;
	}

	public void setVencimentoDiaNaoUtil(
			DominioVencimentoDiaNaoUtil vencimentoDiaNaoUtil) {
		this.vencimentoDiaNaoUtil = vencimentoDiaNaoUtil;
	}

	public DominioDiasMes getInicioPeriodo() {
		return inicioPeriodo;
	}

	public void setInicioPeriodo(DominioDiasMes inicioPeriodo) {
		this.inicioPeriodo = inicioPeriodo;
	}

	public DominioDiasMes getFimPeriodo() {
		return fimPeriodo;
	}

	public void setFimPeriodo(DominioDiasMes fimPeriodo) {
		this.fimPeriodo = fimPeriodo;
	}

	public DominioDiasMes getDiaVencimento() {
		return diaVencimento;
	}

	public void setDiaVencimento(DominioDiasMes diaVencimento) {
		this.diaVencimento = diaVencimento;
	}

	public DominioMesVencimento getMesVencimento() {
		return mesVencimento;
	}

	public void setMesVencimento(DominioMesVencimento mesVencimento) {
		this.mesVencimento = mesVencimento;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public FormattedFields getFormattedFields(){
		return this.formattedFields;
	}

	public void setFormattedFields( FormattedFields formattedFields ){
		this.formattedFields = formattedFields;
	}
}
