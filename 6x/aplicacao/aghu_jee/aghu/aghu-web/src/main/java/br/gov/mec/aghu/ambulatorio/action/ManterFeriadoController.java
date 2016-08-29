package br.gov.mec.aghu.ambulatorio.action;

import java.util.Date;

import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateFormatUtil;



public class ManterFeriadoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2256949309481581863L;

	public final static String FORMATO_DATA_DDMMYY = "dd/MM/yyyy";
	private final String PAGE_FERIADO_LIST = "manterFeriadoList";
	
	@EJB
	private IAghuFacade aghuFacade;
	
	
	private AghFeriados feriado;

	private DominioOperacoesJournal operacao;
	private Date data;
	
	
	public void iniciar() {
	 

	 

		if(data != null) {
			this.feriado = this.aghuFacade.obterFeriado(data);
			this.operacao = DominioOperacoesJournal.UPD;
		} else {
			this.feriado = new AghFeriados();
			this.operacao = DominioOperacoesJournal.INS;
		}
	
	}
	
	
	
	public String cancelar() {
		this.data = null;
		limpar();
		return PAGE_FERIADO_LIST;
	}
	
	public String gravar() {
		try {
			this.aghuFacade.persistirFeriado(this.feriado, this.operacao);
			
			if(this.operacao.equals(DominioOperacoesJournal.INS)) {
				apresentarMsgNegocio(Severity.INFO, "MSG_FERIADO_GRAVADO_SUCESSO", this.obterDataFormatada(this.feriado.getData()));
			} else if(this.operacao.equals(DominioOperacoesJournal.UPD)) {
				apresentarMsgNegocio(Severity.INFO, "MSG_FERIADO_ALTERADO_SUCESSO",  this.obterDataFormatada(this.feriado.getData()));
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null; 
		}
		
		return cancelar();
	}
	
	
	public void limpar() {
		this.feriado = new AghFeriados();
	}
	
	private String obterDataFormatada(Date data) {
		return DateFormatUtil.fomataDiaMesAno(data);
	}
	
	public AghFeriados getFeriado() {
		return feriado;
	}

	public void setFeriado(AghFeriados feriado) {
		this.feriado = feriado;
	} 

	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
}
