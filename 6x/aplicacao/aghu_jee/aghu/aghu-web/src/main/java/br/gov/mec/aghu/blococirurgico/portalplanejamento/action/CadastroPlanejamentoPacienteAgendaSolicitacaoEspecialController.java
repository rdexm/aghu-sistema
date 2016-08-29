package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.vo.AgendaCirurgiaSolicitacaoEspecialVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcAgendaSolicEspecial;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcNecessidadeCirurgica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class CadastroPlanejamentoPacienteAgendaSolicitacaoEspecialController extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3876013242215020562L;
	
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private CadastroPlanejamentoPacienteAgendaController principalController;
	
	private String descricaoAgenda;
	private MbcNecessidadeCirurgica necessidadeCirurgica;
	private Boolean modificouSolicEspecial;
	private String unidadeAvisada;
	private Boolean descricaoObrigatoria;
	private List<AgendaCirurgiaSolicitacaoEspecialVO> agendasSolicEspecial;
	private List<MbcAgendaSolicEspecial> listaAgendasSolicEspecRemover = new ArrayList<MbcAgendaSolicEspecial>();
	
	private AgendaCirurgiaSolicitacaoEspecialVO itemSelecionado;
	
	private Integer identificador;
	
	public void inicio() {
		identificador = 0;
		limparCampos();
		pesquisar();
	}
	
	public List<MbcNecessidadeCirurgica> pesquisarNecessidade(String objParam) {
		return this.returnSGWithCount(this.blocoCirurgicoCadastroApoioFacade.buscarNecessidadeCirurgicaPorCodigoOuDescricao((String) objParam, false),pesquisarNecessidadeCount(objParam));
	}
	
	public Long pesquisarNecessidadeCount(String objParam) {
		return this.blocoCirurgicoCadastroApoioFacade.countBuscarNecessidadeCirurgicaPorCodigoOuDescricao((String) objParam, false);
	}
	
	public void carregarDescricaoUnf() {
		descricaoObrigatoria = necessidadeCirurgica.getIndExigeDescSolic();
		if(necessidadeCirurgica.getAghUnidadesFuncionais() != null) {
			unidadeAvisada = necessidadeCirurgica.getAghUnidadesFuncionais().getDescricao();
		} else {
			unidadeAvisada = null;
		}
	}
	
	public void removerDescricaoUnf() {
		descricaoObrigatoria = false;
		unidadeAvisada = null;
	}
	
	public void limparCampos() {
		descricaoAgenda = null;
		necessidadeCirurgica = null;
		unidadeAvisada = null;
		descricaoObrigatoria = false;
		modificouSolicEspecial = false;
		itemSelecionado = null;		
	}
	
	public void pesquisar() {
		agendasSolicEspecial = blocoCirurgicoPortalPlanejamentoFacade.buscarMbcAgendaCirurgiaSolicEspecialPorAgdSeq(getAgenda().getSeq()); 
		modificouSolicEspecial = false;
		listaAgendasSolicEspecRemover = new ArrayList<MbcAgendaSolicEspecial>();
	}
	
	public void removerAgendaSolicEspecial(AgendaCirurgiaSolicitacaoEspecialVO agendaSolicEspecial) {
		modificouSolicEspecial = true;
		
		if(getAgenda().getSeq() != null && agendaSolicEspecial.getNciSeq() != null && agendaSolicEspecial.getSeqp() != null) {
			listaAgendasSolicEspecRemover.add(blocoCirurgicoPortalPlanejamentoFacade.obterMbcAgendaSolicEspecialPorNciSeqUnfseq(getAgenda().getSeq(), 
					agendaSolicEspecial.getNciSeq(), agendaSolicEspecial.getSeqp()));
		}
		
		getAgendasSolicEspecial().remove(agendaSolicEspecial);
	}
	
	public void adicionar() {
		modificouSolicEspecial = true;
		
		if(necessidadeCirurgica != null) {
			AgendaCirurgiaSolicitacaoEspecialVO solicitacao = new AgendaCirurgiaSolicitacaoEspecialVO();
			solicitacao.setIdentificador(++identificador);
			solicitacao.setNciSeq(necessidadeCirurgica.getSeq());
			solicitacao.setNciDescricao(necessidadeCirurgica.getDescricao());
			solicitacao.setMseDescricao(descricaoAgenda);
			
			if (necessidadeCirurgica.getAghUnidadesFuncionais() != null) {
				solicitacao.setUnfSeq(necessidadeCirurgica.getAghUnidadesFuncionais().getSeq());
				solicitacao.setUnfDescricao(necessidadeCirurgica.getAghUnidadesFuncionais().getDescricao());
			}
			
			if(getAgendasSolicEspecial() == null) {
				setAgendasSolicEspecial(new ArrayList<AgendaCirurgiaSolicitacaoEspecialVO>());
			}
			
			getAgendasSolicEspecial().add(solicitacao);
			limparCampos();
		}
	}
	
	public List<MbcAgendaSolicEspecial> obterAgendasSolicitacaoEspecial() {
		List<MbcAgendaSolicEspecial> agendasSolicitacao = new ArrayList<MbcAgendaSolicEspecial>();
		MbcAgendaSolicEspecial agenda = new MbcAgendaSolicEspecial();
		
		for (AgendaCirurgiaSolicitacaoEspecialVO agendaCirurgia : agendasSolicEspecial) {
			agenda = new MbcAgendaSolicEspecial();
			
			if(agendaCirurgia.getAgdSeq() != null && agendaCirurgia.getSeqp() != null && agendaCirurgia.getNciSeq() != null) {
				agenda = blocoCirurgicoPortalPlanejamentoFacade.obterMbcAgendaSolicEspecialPorNciSeqUnfseq(agendaCirurgia.getAgdSeq(), 
						agendaCirurgia.getNciSeq(), agendaCirurgia.getSeqp());
			} else {
				agenda.setMbcAgendas(getAgenda());
				agenda.setDescricao(agendaCirurgia.getMseDescricao());
				agenda.setMbcNecessidadeCirurgica(blocoCirurgicoPortalPlanejamentoFacade.obterMbcNecessidadeCirurgicaPorId(agendaCirurgia.getNciSeq(), null, 
						new Enum[]{MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS}));
			}
			agendasSolicitacao.add(agenda);
		}

		return agendasSolicitacao;
	}
	
	public void carregarNecessidadeAPCongelado() {
		try {
			if(parametroFacade.verificarExisteAghParametro(AghuParametrosEnum.P_AP_CONGELACAO)){
				AghParametros pCongelacao = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AP_CONGELACAO);
				if(pCongelacao != null && pCongelacao.getVlrNumerico() != null) {
					MbcNecessidadeCirurgica necessidadeCirg = pesquisarNecessidade(pCongelacao.getVlrNumerico().toString()).get(0);
					if(necessidadeCirg != null) {
						for(AgendaCirurgiaSolicitacaoEspecialVO item : getAgendasSolicEspecial()) {
							if(item.getNciSeq().equals(necessidadeCirg.getSeq())) {
								return;
							}
						}
						necessidadeCirurgica = necessidadeCirg;
						descricaoAgenda = null;
						adicionar();
					}
				}
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String abreviar(String str, int maxWidth) {
		if (str != null && str.length() > maxWidth) {
			str = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return str;
	}
	
	public Boolean isAbreviar(String str, int maxWidth) {
		Boolean abreviar = Boolean.FALSE;
		if (str != null) {
			abreviar = str.length() > maxWidth;
		}
		return abreviar;
	}
	
	public MbcAgendas getAgenda() {
		return principalController.getAgenda();
	}
	
	public MbcNecessidadeCirurgica getNecessidadeCirurgica() {
		return necessidadeCirurgica;
	}

	public void setNecessidadeCirurgica(MbcNecessidadeCirurgica necessidadeCirurgica) {
		this.necessidadeCirurgica = necessidadeCirurgica;
	}
	
	public List<AgendaCirurgiaSolicitacaoEspecialVO> getAgendasSolicEspecial() {
		return agendasSolicEspecial;
	}

	public void setAgendasSolicEspecial(List<AgendaCirurgiaSolicitacaoEspecialVO> agendasSolicEspecial) {
		this.agendasSolicEspecial = agendasSolicEspecial;
	}

	public Boolean getModificouSolicEspecial() {
		return modificouSolicEspecial;
	}

	public void setModificouSolicEspecial(Boolean modificouSolicEspecial) {
		this.modificouSolicEspecial = modificouSolicEspecial;
	}

	public String getUnidadeAvisada() {
		return unidadeAvisada;
	}

	public void setUnidadeAvisada(String unidadeAvisada) {
		this.unidadeAvisada = unidadeAvisada;
	}

	public Boolean getDescricaoObrigatoria() {
		return descricaoObrigatoria;
	}

	public void setDescricaoObrigatoria(Boolean descricaoObrigatoria) {
		this.descricaoObrigatoria = descricaoObrigatoria;
	}

	public String getDescricaoAgenda() {
		return descricaoAgenda;
	}

	public void setDescricaoAgenda(String descricaoAgenda) {
		this.descricaoAgenda = descricaoAgenda;
	}

	public List<MbcAgendaSolicEspecial> getListaAgendasSolicEspecRemover() {
		return listaAgendasSolicEspecRemover;
	}

	public void setListaAgendasSolicEspecRemover(List<MbcAgendaSolicEspecial> listaAgendasSolicEspecRemover) {
		this.listaAgendasSolicEspecRemover = listaAgendasSolicEspecRemover;
	}

	public AgendaCirurgiaSolicitacaoEspecialVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(AgendaCirurgiaSolicitacaoEspecialVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public Integer getIdentificador() {
		return identificador;
	}

	public void setIdentificador(Integer identificador) {
		this.identificador = identificador;
	}
}
