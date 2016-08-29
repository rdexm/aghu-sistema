package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaSalasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasAgendaEscalaDiaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasAgendaEscalaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;


public class VisualizarAgendaEscalaController extends ActionController {

	private static final String _HIFEN_ = " - ";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 4611358810924240384L;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	private List<EscalaSalasVO> escala;
	private PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO;
	private PortalPesquisaCirurgiasAgendaEscalaDiaVO agendasEscalas;
	PortalPesquisaCirurgiasAgendaEscalaDiaVO resultado = null;
	private Date dataInicio;
	private Date dataFim;
	
	private String urlVoltar;
	
	private Boolean readOnlyAvancar = false;
	private Boolean readOnlyRetroceder = false;
	
	private String especialidadeTooltip;
	private String procedimentoToolTip;
	private String pacienteToolTip;
	
	public void recebeParametros(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO) {
		this.portalPesquisaCirurgiasParametrosVO = portalPesquisaCirurgiasParametrosVO;
		this.buscarAgendasEscala();
	}
	
	public void buscarAgendasEscala(){
		try {
			agendasEscalas = blocoCirurgicoPortalPlanejamentoFacade.pesquisarAgendasEscalasDia(portalPesquisaCirurgiasParametrosVO, null, false);
			
			if(!agendasEscalas.getPrimeiroDia().isEmpty()){
				readOnlyRetroceder = false;
			} 
			
			if(!agendasEscalas.getTerceiroDia().isEmpty()){
				readOnlyAvancar = false;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void avancar() throws ApplicationBusinessException {
		
		try{
			Date dataFinal = null;
			if(portalPesquisaCirurgiasParametrosVO.getDataInicio() != null){
				dataFinal = portalPesquisaCirurgiasParametrosVO.getDataFim();
			}else{
				if(portalPesquisaCirurgiasParametrosVO.getPacCodigo() != null){
					List<Date> datas = blocoCirurgicoPortalPlanejamentoFacade.buscarTodasDatasPaciente(portalPesquisaCirurgiasParametrosVO);
					dataFinal = datas.get(datas.size()-1);
				}
			}
			
			if(agendasEscalas.getDataAgendaDate()[2] != null && DateUtil.isDatasIguais(agendasEscalas.getDataAgendaDate()[2], dataFinal)){
				this.apresentarMsgNegocio(Severity.ERROR, "MBC_01053");
				readOnlyAvancar = true;
			} else {
				resultado = blocoCirurgicoPortalPlanejamentoFacade.pesquisarAgendasEscalasDia(portalPesquisaCirurgiasParametrosVO,
							DateUtil.adicionaDias(agendasEscalas.getDataAgendaDate()[0], 1), false);
				if(resultado.getTerceiroDia().isEmpty()){
					this.apresentarMsgNegocio(Severity.ERROR, "MBC_01053");
					readOnlyAvancar = true;
				} else {
					agendasEscalas = resultado;
					readOnlyRetroceder = false;
				}
			}
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void retroceder() throws ApplicationBusinessException {
		try{
			Date dataInicio = null;
			Date dataParametro = null;
			if(portalPesquisaCirurgiasParametrosVO.getDataInicio() != null){
				dataInicio = portalPesquisaCirurgiasParametrosVO.getDataInicio();
				dataParametro = agendasEscalas.getDataAgendaDate()[2];
			}else{
				if(portalPesquisaCirurgiasParametrosVO.getPacCodigo() != null){
					List<Date> datas = blocoCirurgicoPortalPlanejamentoFacade.buscarTodasDatasPaciente(portalPesquisaCirurgiasParametrosVO);
					dataInicio = datas.get(0);
					dataParametro = DateUtil.adicionaDias(agendasEscalas.getDataAgendaDate()[2], -1);
				}
			}
			
			if(agendasEscalas.getDataAgendaDate()[0] != null && DateUtil.isDatasIguais(agendasEscalas.getDataAgendaDate()[0], dataInicio)){
				this.apresentarMsgNegocio(Severity.ERROR, "MBC_01052");
				readOnlyRetroceder = true;
			} else {
				resultado = blocoCirurgicoPortalPlanejamentoFacade.pesquisarAgendasEscalasDia(portalPesquisaCirurgiasParametrosVO, 
						dataParametro, true);
				
				if(resultado.getPrimeiroDia().isEmpty()){
					this.apresentarMsgNegocio(Severity.ERROR, "MBC_01052");
					readOnlyRetroceder = true;
				} else {
					agendasEscalas = resultado;
					readOnlyAvancar = false;
				}
			}
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String voltar() {
		return getUrlVoltar();
	}
	
	public String colorirTabela(Object obj) {
		StringBuffer retorno = new StringBuffer();
		if(obj instanceof PortalPesquisaCirurgiasAgendaEscalaVO) {
			PortalPesquisaCirurgiasAgendaEscalaVO item = (PortalPesquisaCirurgiasAgendaEscalaVO) obj;
			
			if(item.getCancelado() != null && item.getCancelado()) {
				retorno.append("agd-cancelada");
			} else if(item.getEscala() != null && item.getEscala()) {
				retorno.append("agd-escala");
			} else if(item.getPlanejado() != null && item.getPlanejado()) {
				retorno.append("agd-planejada");
			} else if(item.getRealizado() != null && item.getRealizado()){
				retorno.append("agd-realizada");
			}
		}
		return retorno.toString();
	}
	
	
	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null){
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	
	public String montarPacienteProntuario(Object obj){
		String paciente ="";
		if(obj instanceof PortalPesquisaCirurgiasAgendaEscalaVO) {
			PortalPesquisaCirurgiasAgendaEscalaVO item = (PortalPesquisaCirurgiasAgendaEscalaVO) obj;
			paciente = item.getNomePaciente()+_HIFEN_+ item.getProntuarioFormatado();
		}
		return abreviar(paciente,54);
	}
	
	public String montarProcedimentoUnidadeSala(Object obj){
		String procedimento ="";
		String sala ="";
		if(obj instanceof PortalPesquisaCirurgiasAgendaEscalaVO) {
			PortalPesquisaCirurgiasAgendaEscalaVO item = (PortalPesquisaCirurgiasAgendaEscalaVO) obj;
			
			if(item.getProcedimento()!=null){
				procedimento = procedimento.concat(item.getProcedimento());
			}
			if(item.getDescricaoUnidade()!=null){
				procedimento = procedimento.concat(_HIFEN_).concat(item.getDescricaoUnidade());
			}	
			
			if(item.getSciSeqp()!=null){
				sala = " - Sala "+item.getSciSeqp();
				procedimento = procedimento.concat(sala);
			}
			
			if(item.getAproveitamentoSala() != null){
				procedimento = procedimento.concat(item.getAproveitamentoSala());
			}
		}	
		return abreviar(procedimento,54);
	}
	
	public String montarEspecialidadeEquipeConvenio(Object obj){
		String especialidade ="";
		if(obj instanceof PortalPesquisaCirurgiasAgendaEscalaVO) {
			PortalPesquisaCirurgiasAgendaEscalaVO item = (PortalPesquisaCirurgiasAgendaEscalaVO) obj;
			especialidade = item.getEspecialidade()+", "+ item.getEquipe()+", "+item.getConvenio();
		}	
		return abreviar(especialidade,54);
	}
	
	public boolean montarToolTip(Object obj){
		
		String sala ="";
		if(obj instanceof PortalPesquisaCirurgiasAgendaEscalaVO) {
			PortalPesquisaCirurgiasAgendaEscalaVO item = (PortalPesquisaCirurgiasAgendaEscalaVO) obj;
			if(item.getSciSeqp()!=null){
				sala = " - Sala "+item.getSciSeqp();
			}
			procedimentoToolTip = item.getProcedimento()+_HIFEN_+ item.getDescricaoUnidade()+sala;
			if(item.getAproveitamentoSala() != null){
				procedimentoToolTip = procedimentoToolTip.concat(item.getAproveitamentoSala());
			}
			especialidadeTooltip = item.getEspecialidade()+", "+ item.getEquipe()+", "+item.getConvenio();
			pacienteToolTip = item.getNomePaciente()+_HIFEN_+ item.getProntuarioFormatado();
			if(procedimentoToolTip.length()>54 || especialidadeTooltip.length()>54 || pacienteToolTip.length()>54){
				return true;
			}
		}	
		return false;
	}
	
	public void setEscala(List<EscalaSalasVO> escala) {
		this.escala = escala;
	}

	public List<EscalaSalasVO> getEscala() {
		return escala;
	}

	public void setUrlVoltar(String urlVoltar) {
		this.urlVoltar = urlVoltar;
	}

	public String getUrlVoltar() {
		return urlVoltar;
	}

	public PortalPesquisaCirurgiasParametrosVO getPortalPesquisaCirurgiasParametrosVO() {
		return portalPesquisaCirurgiasParametrosVO;
	}


	public void setPortalPesquisaCirurgiasParametrosVO(
			PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO) {
		this.portalPesquisaCirurgiasParametrosVO = portalPesquisaCirurgiasParametrosVO;
	}

	public PortalPesquisaCirurgiasAgendaEscalaDiaVO getAgendasEscalas() {
		return agendasEscalas;
	}

	public void setAgendasEscalas(
			PortalPesquisaCirurgiasAgendaEscalaDiaVO agendasEscalas) {
		this.agendasEscalas = agendasEscalas;
	}
	
	public void setReadOnlyRetroceder(Boolean readOnlyRetroceder) {
		this.readOnlyRetroceder = readOnlyRetroceder;
	}

	public Boolean getReadOnlyRetroceder() {
		return readOnlyRetroceder;
	}

	public void setReadOnlyAvancar(Boolean readOnlyAvancar) {
		this.readOnlyAvancar = readOnlyAvancar;
	}

	public Boolean getReadOnlyAvancar() {
		return readOnlyAvancar;
	}
	
	public String getEspecialidadeTooltip() {
		return especialidadeTooltip;
	}

	public void setEspecialidadeTooltip(String especialidadeTooltip) {
		this.especialidadeTooltip = especialidadeTooltip;
	}

	public String getProcedimentoToolTip() {
		return procedimentoToolTip;
	}

	public void setProcedimentoToolTip(String procedimentoToolTip) {
		this.procedimentoToolTip = procedimentoToolTip;
	}

	public String getPacienteToolTip() {
		return pacienteToolTip;
	}

	public void setPacienteToolTip(String pacienteToolTip) {
		this.pacienteToolTip = pacienteToolTip;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataInicio() {
		return dataInicio;
	}
}
