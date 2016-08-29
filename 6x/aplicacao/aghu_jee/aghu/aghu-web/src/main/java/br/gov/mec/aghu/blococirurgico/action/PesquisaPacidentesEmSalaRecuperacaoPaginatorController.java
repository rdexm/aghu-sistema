package br.gov.mec.aghu.blococirurgico.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.vo.PacientesEmSalaRecuperacaoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDestinoPaciente;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaPacidentesEmSalaRecuperacaoPaginatorController extends
		ActionController implements ActionPaginator {

	

	@Inject @Paginator
	private DynamicDataModel<PacientesEmSalaRecuperacaoVO> dataModel;

	private static final Log LOG = LogFactory.getLog(PesquisaPacidentesEmSalaRecuperacaoPaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -4615760901841389986L;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	
	// Campos de filtro
	private AghUnidadesFuncionais unidadeFuncional;

	private Date dataEntradaSr;

	//
	private MbcCirurgias cirurgia;
	private Integer crgSeq;
	private String cameFrom;
	private Boolean ativo;
	private Date dataHoraSaida = null;
	private MbcDestinoPaciente destinoPaciente;

	private Boolean apresentaMsgModal = false;

	private List<PacientesEmSalaRecuperacaoVO> pacientesEmSR;

	private PacientesEmSalaRecuperacaoVO pacienteEmSRSelecionado;
	
	AghParametros parametroDestPaciente = null;
	
	private String nomeMicrocomputador;
	
	private final String PAGE_EDITAR_PACIENTE_SALA_RECUPERACAO = "alteraPacienteEmSalaRecuperacao";
	private final String PAGE_DETALHAR_REGISTRO_CIRURGIA = "detalhaRegistroCirurgia";
	

	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 inicio();
	}
	
	/**
	 * Chamado no inicio de cada conversação	 * 
	 */
	public void inicio()  {
		this.dataModel.setDefaultMaxRow(40);
		
		try {
			parametroDestPaciente = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DEST_SALA_REC);
		} catch (ApplicationBusinessException e1) {
			LOG.error("Exceção capturada:", e1);
		}
		
		try {
			
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção capturada:", e);
			}
			
			unidadeFuncional = blocoCirurgicoFacade.obterUnidadeFuncionalCirurgia(nomeMicrocomputador);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	

		if (unidadeFuncional != null) {
			this.dataModel.reiniciarPaginator();
		}

	}

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.dataModel.setDefaultMaxRow(40);
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		unidadeFuncional = null;
		dataEntradaSr = null;
		this.dataModel.limparPesquisa();

	}

	@Override
	public List<PacientesEmSalaRecuperacaoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			return this.blocoCirurgicoFacade.listarPacientesEmSR(firstResult,maxResult, orderProperty, asc, unidadeFuncional, dataEntradaSr);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			return null;
		}
	}

	@Override
	public Long recuperarCount() {
		try {
			return this.blocoCirurgicoFacade.listarPacientesEmSRCount(unidadeFuncional, dataEntradaSr);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			return null;
		}
	}

	/**
	 * Obtem unidade funcional
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesCirurgicas(
			String objPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoCadastroApoioFacade
				.buscarUnidadesFuncionaisCirurgiaSB(objPesquisa),pesquisarUnidadesCirurgicasCount(objPesquisa));
	}

	public Long pesquisarUnidadesCirurgicasCount(String objPesquisa) {
		return blocoCirurgicoCadastroApoioFacade
				.contarUnidadesFuncionaisCirurgia(objPesquisa);
	}

	// Acoes

	public String editarItem() {		
		return PAGE_EDITAR_PACIENTE_SALA_RECUPERACAO;
	}

	public String detalharCirurgia() {		
		return PAGE_DETALHAR_REGISTRO_CIRURGIA;
	}

	// Modal Registrar Saida de Paciente de SR

	public void setarDadosSaidaSR(Integer crgSeq) {
		cirurgia = blocoCirurgicoFacade.obterCirurgiaPorSeq(crgSeq);
		dataHoraSaida = new Date();
		destinoPaciente = null;
	}

	public void registrarSaidaPacienteSalaRecuperacao() {
		cirurgia.setDestinoPaciente(destinoPaciente);
		cirurgia.setDataSaidaSr(dataHoraSaida);

		apresentaMsgModal = false;
		String labelDestino = getBundle().getString("LABEL_DESTINO");
		String labelDataHoraSaida = getBundle().getString(
				"LABEL_DESTINO_DATA_HORA_SAIDA");
		if (dataHoraSaida == null) {
			apresentarMsgNegocio("dataHoraSaida",
					Severity.ERROR, "CAMPO_OBRIGATORIO", labelDataHoraSaida);
			setApresentaMsgModal(true);
		}
		if (destinoPaciente == null) {
			apresentarMsgNegocio("destinoPaciente", Severity.ERROR, "CAMPO_OBRIGATORIO",
					labelDestino);
			setApresentaMsgModal(true);
		}
		if (!apresentaMsgModal) {
			this.gravar();
		}	
		
	}

	public void gravar() {
		String msg = " ";
		try {
			if (!apresentaMsgModal) {
				msg = blocoCirurgicoFacade.gravarMbcCirurgias(cirurgia);
				apresentarMsgNegocio(Severity.INFO, msg);				
				this.dataModel.reiniciarPaginator();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			this.pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		
	}
	
	public List<MbcDestinoPaciente> pesquisarDestinoPacientePorSeqOuDescricao(String objPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoFacade.pesquisarDestinoPacientePorSeqOuDescricao(objPesquisa, 0, 100, null, true, parametroDestPaciente.getVlrNumerico().byteValue()),pesquisarDestinoPacientePorSeqOuDescricaoCount(objPesquisa));
	}

	public Long pesquisarDestinoPacientePorSeqOuDescricaoCount(String objPesquisa) {
		return blocoCirurgicoFacade.pesquisarDestinoPacientePorSeqOuDescricaoCount((String) objPesquisa);
	}

	/*
	 * Acessadores
	 */

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public void setPacienteEmSR(List<PacientesEmSalaRecuperacaoVO> pacienteEmSR) {
		this.pacientesEmSR = pacienteEmSR;
	}

	public List<PacientesEmSalaRecuperacaoVO> getPacienteEmSR() {
		return pacientesEmSR;
	}

	public void setPacienteEmSRSelecionado(
			PacientesEmSalaRecuperacaoVO pacienteEmSRSelecionado) {
		this.pacienteEmSRSelecionado = pacienteEmSRSelecionado;
	}

	public PacientesEmSalaRecuperacaoVO getPacienteEmSRSelecionado() {
		return pacienteEmSRSelecionado;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public Date getDataEntradaSr() {
		return dataEntradaSr;
	}

	public List<PacientesEmSalaRecuperacaoVO> getPacientesEmSR() {
		return pacientesEmSR;
	}

	public void setBlocoCirurgicoFacade(
			IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public void setDataEntradaSr(Date dataEntradaSr) {
		this.dataEntradaSr = dataEntradaSr;
	}

	public void setPacientesEmSR(
			List<PacientesEmSalaRecuperacaoVO> pacientesEmSR) {
		this.pacientesEmSR = pacientesEmSR;
	}

	public void setDataHoraSaida(Date dataHoraSaida) {
		this.dataHoraSaida = dataHoraSaida;
	}

	public Date getDataHoraSaida() {
		return dataHoraSaida;
	}

	public Boolean getApresentaMsgModal() {
		return apresentaMsgModal;
	}

	public void setApresentaMsgModal(Boolean apresentaMsgModal) {
		this.apresentaMsgModal = apresentaMsgModal;
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	public MbcDestinoPaciente getDestinoPaciente() {
		return destinoPaciente;
	}

	public void setDestinoPaciente(MbcDestinoPaciente destinoPaciente) {
		this.destinoPaciente = destinoPaciente;
	} 


	public DynamicDataModel<PacientesEmSalaRecuperacaoVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<PacientesEmSalaRecuperacaoVO> dataModel) {
	 this.dataModel = dataModel;
	}
}