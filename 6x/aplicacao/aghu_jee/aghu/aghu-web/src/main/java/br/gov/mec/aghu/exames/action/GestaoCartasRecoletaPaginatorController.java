package br.gov.mec.aghu.exames.action;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoCartaColeta;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelExtratoItemCartas;
import br.gov.mec.aghu.model.AelItemSolicCartas;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;


public class GestaoCartasRecoletaPaginatorController extends ActionController implements ActionPaginator  {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AelItemSolicCartas> dataModel;

	private static final Log LOG = LogFactory.getLog(GestaoCartasRecoletaPaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 4375862282112364375L;

	@EJB
	private IExamesFacade examesFacade;
	

	@EJB 
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private EmitirCartaRecoletaController emitirCartaRecoletaController;

	private DominioSituacaoCartaColeta situacao;
	private Date dtInicio;
	private Date dtFim;
	private Integer iseSoeSeq;
	private Integer pacCodigo;
	private Integer prontuario;
	private AipPacientes paciente;
	
	private List<AelExtratoItemCartas> extrato;

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void inicio() {
	 

		if(pacCodigo != null) {
			paciente = pacienteFacade.obterPacientePorCodigo(pacCodigo);
			prontuario = paciente.getProntuario();
		}
	
	}
	
	public void pesquisar() {
		try {
			examesFacade.validarPesquisaCartasRecoleta(situacao, dtInicio, dtFim, iseSoeSeq, pacCodigo);
			this.dataModel.setPesquisaAtiva(true);
			this.dataModel.reiniciarPaginator();
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limpar() {
		situacao = null;
		dtInicio = null;
		dtFim = null;
		iseSoeSeq = null;
		paciente = null;
		pacCodigo = null;
		prontuario = null;
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public String redirecionarPesquisaFonetica(){
		return "paciente-pesquisaPacienteComponente";
	}

	@Override
	public Long recuperarCount() {
		return this.examesFacade.listarAelItemSolicCartasPorSituacaoDtInicioEFimEPacCodigoCount(situacao, dtInicio, dtFim, iseSoeSeq, pacCodigo);
	}

	@Override
	public List<AelItemSolicCartas> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.examesFacade.listarAelItemSolicCartasPorSituacaoDtInicioEFimEPacCodigo(firstResult, maxResult, orderProperty, asc, situacao, dtInicio, dtFim, iseSoeSeq, pacCodigo);
	}

	public void extratoCarta(AelItemSolicCartas carta) {
		if(carta.getAelExtratoItemCartases() != null) {
			this.extrato = this.examesFacade.buscarAelExtratoItemCartasPorItemCartasComMotivoRetorno(carta);
			final BeanComparator dthrEventoSorter = new BeanComparator("dthrEvento", new ReverseComparator(new NullComparator(false)));
			Collections.sort(extrato, dthrEventoSorter);
		}
	}
	
	public void emitirCarta(AelItemSolicCartas carta) {
		emitirCartaRecoletaController.setIseSoeSeq(carta.getId().getIseSoeSeq());
		emitirCartaRecoletaController.setIseSeqp(carta.getId().getIseSeqp());
		emitirCartaRecoletaController.setSeqp(carta.getId().getSeqp());
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			emitirCartaRecoletaController.directPrint();
			//ATUALIZA SITUAÇÃO
			carta.setSituacao(DominioSituacaoCartaColeta.EM);
			carta.setMotivoRetorno(null);
			this.examesFacade.atualizarAelItemSolicCartas(carta, nomeMicrocomputador);
			
			this.dataModel.reiniciarPaginator();			
		
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public DominioSituacaoCartaColeta getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoCartaColeta situacao) {
		this.situacao = situacao;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public List<AelExtratoItemCartas> getExtrato() {
		return extrato;
	}

	public void setExtrato(List<AelExtratoItemCartas> extrato) {
		this.extrato = extrato;
	} 


	public DynamicDataModel<AelItemSolicCartas> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelItemSolicCartas> dataModel) {
	 this.dataModel = dataModel;
	}
}
