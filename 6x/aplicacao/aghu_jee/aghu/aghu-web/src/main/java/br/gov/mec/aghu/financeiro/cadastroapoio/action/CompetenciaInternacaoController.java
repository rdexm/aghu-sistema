package br.gov.mec.aghu.financeiro.cadastroapoio.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CompetenciaInternacaoController extends ActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1487887222126108629L;
	private FatCompetencia competencia;
	private final String PAGE_PESQUISAR_COMPETENCIA_INTERNACAO = "competenciaInternacaoList";
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private Integer mes;
	private Integer ano;
	private Date dtHrInicio;
	private DominioModuloCompetencia modulo;
	private DominioSimNao indFaturado;
	
	@PostConstruct
	public void init() {
	 

		competencia = faturamentoFacade.obterCompetenciaModuloMesAnoDtHoraInicioSemHora(modulo, mes, ano, dtHrInicio);
		indFaturado = DominioSimNao.getInstance( (competencia!=null && competencia.getIndFaturado() ) );
	
	}
	
	public String salvar(){
		try {
			faturamentoFacade.atualizarFatCompetencia(getCompetencia());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_COMPETENCIA");
			return PAGE_PESQUISAR_COMPETENCIA_INTERNACAO;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}

	public String cancelar() {
		this.setCompetencia(new FatCompetencia());
		return PAGE_PESQUISAR_COMPETENCIA_INTERNACAO;
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}
	
	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
	}
	
	public Integer getMes() {
		return mes;
	}
	
	public void setMes(Integer mes) {
		this.mes = mes;
	}
	
	public Integer getAno() {
		return ano;
	}
	
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	
	public Date getDtHrInicio() {
		return dtHrInicio;
	}
	
	public void setDtHrInicio(Date dtHrInicio) {
		this.dtHrInicio = dtHrInicio;
	}
	
	public DominioModuloCompetencia getModulo() {
		return modulo;
	}
	
	public void setModulo(DominioModuloCompetencia modulo) {
		this.modulo = modulo;
	}

	public DominioSimNao getIndFaturado() {
		return indFaturado;
	}

	public void setIndFaturado(DominioSimNao indFaturado) {
		this.indFaturado = indFaturado;
	}
}
