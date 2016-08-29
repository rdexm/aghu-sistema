package br.gov.mec.aghu.paciente.prontuario.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.vo.ProntuarioCirurgiaVO;
import br.gov.mec.aghu.internacao.vo.ProntuarioInternacaoVO;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipSolicitacaoProntuarios;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.AipMovimentacaoProntuarioVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class MonitoramentoDesarquivamentoProntuarioController extends ActionController {

	private static final String MENSAGEM_NENHUMA_MOVIMENTACAO_SELECIONADA = "MENSAGEM_NENHUMA_MOVIMENTACAO_SELECIONADA";

	private static final long serialVersionUID = 7151798855844156244L;
	
	private static final Log LOG = LogFactory.getLog(MonitoramentoDesarquivamentoProntuarioController.class);	
	
	@EJB
	private IPacienteFacade pacienteFacade;

	@Inject
	private RelatorioMonitorDesarquivamentoProntuarioController relatorioMonitorDesarquivamentoProntuarioController;

	@Inject
	private RelatorioDesarquivamentoProntuarioInternacaoController relatorioDesarquivamentoProntuarioInternacaoController;
	
	@Inject
	private RelatorioDesarquivamentoProntuarioCirurgiaController relatorioDesarquivamentoProntuarioCirurgiaController;

	private Integer abaSelecionada;
	
	private List<AipMovimentacaoProntuarioVO> listaMovimentacaoVO;
	private List<ProntuarioInternacaoVO> prontuariosInternacao;
	private List<ProntuarioCirurgiaVO> listaCirurgiaVO;
	
	private List<ProntuarioInternacaoVO> listaInternacao;

	@PostConstruct
	public void init() {
		begin(this.conversation);
		reiniciar();
	}

	public void reiniciar(){
		if(listaMovimentacaoVO != null){
			listaMovimentacaoVO.clear();
		}
		setListaInternacao(pacienteFacade.pesquisarAvisoInternacaoSAMES());
	}
	
	public void buscarProntuario() {

		List<AipMovimentacaoProntuarios> listaMovimentacoesSelecionadas = new ArrayList<AipMovimentacaoProntuarios>();

		if (listaMovimentacaoVO == null) {
			this.apresentarMsgNegocio(Severity.INFO,MENSAGEM_NENHUMA_MOVIMENTACAO_SELECIONADA);
		} else {
			for (AipMovimentacaoProntuarioVO voMovimentacao : listaMovimentacaoVO) {
				if (voMovimentacao.getSelecionado()) {
					listaMovimentacoesSelecionadas.add(voMovimentacao.getMovimentacao());
				}
			}
			if (listaMovimentacoesSelecionadas.isEmpty()) {
				this.apresentarMsgNegocio(Severity.INFO,
						MENSAGEM_NENHUMA_MOVIMENTACAO_SELECIONADA);
			}

			try {			
				this.pacienteFacade.buscarProntuariosDesarquivamentoProntuario(listaMovimentacoesSelecionadas);
			} catch (ApplicationBusinessException  e) {
				LOG.error(e.getMessage(), e);
				this.apresentarExcecaoNegocio(e);
			}
		}

		reiniciar();
	}

	public void buscarProntuarioInternacao() {

		List<ProntuarioInternacaoVO> listaProntuariosSelecionados = new ArrayList<ProntuarioInternacaoVO>();

		if(prontuariosInternacao!=null) {
			for (ProntuarioInternacaoVO voProntuario : prontuariosInternacao) {
				listaProntuariosSelecionados.add(voProntuario);
			}
		}

		if (listaProntuariosSelecionados.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO,
					MENSAGEM_NENHUMA_MOVIMENTACAO_SELECIONADA);

		}
		
		
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().toString();
		} catch (UnknownHostException uhe) {
			LOG.error("Problema ao obter endereço de rede do host remoto", uhe);
		}
		
		this.pacienteFacade.buscarProntuariosInternacaoDesarquivamentoProntuario(listaProntuariosSelecionados, nomeMicrocomputador, new Date());

		this.reiniciar();

	}

	public String buscarImprimirProntuario() throws ApplicationBusinessException {

		List<AipMovimentacaoProntuarios> listaMovimentacoesSelecionadas = new ArrayList<AipMovimentacaoProntuarios>();

		AipSolicitacaoProntuarios solicitacao;

		if (listaMovimentacaoVO == null) {
			this.apresentarMsgNegocio(Severity.INFO,MENSAGEM_NENHUMA_MOVIMENTACAO_SELECIONADA);
			return null;
		}
		for (AipMovimentacaoProntuarioVO voMovimentacao : listaMovimentacaoVO) {
			if (voMovimentacao.getSelecionado()) {
				listaMovimentacoesSelecionadas.add(voMovimentacao.getMovimentacao());
			}
		}

		if (listaMovimentacoesSelecionadas.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO,MENSAGEM_NENHUMA_MOVIMENTACAO_SELECIONADA);
			return null;
		}

		solicitacao = this.pacienteFacade.buscarProntuariosDesarquivamentoProntuario(listaMovimentacoesSelecionadas);

		this.reiniciar();

		this.relatorioMonitorDesarquivamentoProntuarioController.setAbaSelecionada(getAbaSelecionada());
		this.relatorioMonitorDesarquivamentoProntuarioController.setListaMovimentacoes(listaMovimentacoesSelecionadas);
		this.relatorioMonitorDesarquivamentoProntuarioController.setSolicitacao(solicitacao);
		
		return this.relatorioMonitorDesarquivamentoProntuarioController.print();
	}

	public String buscarImprimirProntuarioInternacao() throws ApplicationBusinessException {

		List<ProntuarioInternacaoVO> listaProntuariosSelecionados = new ArrayList<ProntuarioInternacaoVO>();

		if(prontuariosInternacao!=null) {
			for (ProntuarioInternacaoVO voProntuario : prontuariosInternacao) {
				if (voProntuario != null) {
					listaProntuariosSelecionados.add(voProntuario);
				}
			}
		}

		if (listaProntuariosSelecionados.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO,
					MENSAGEM_NENHUMA_MOVIMENTACAO_SELECIONADA);
			return null;
		}
		
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = this.getEnderecoIPv4HostRemoto().toString();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}

		this.pacienteFacade.buscarProntuariosInternacaoDesarquivamentoProntuario(listaProntuariosSelecionados, nomeMicrocomputador, new Date());

		this.reiniciar();

		this.relatorioDesarquivamentoProntuarioInternacaoController.setAbaSelecionada(getAbaSelecionada());
		this.relatorioDesarquivamentoProntuarioInternacaoController.setListaProntuarios(listaProntuariosSelecionados);
		return this.relatorioDesarquivamentoProntuarioInternacaoController.print();
	}
	
	public String buscarImprimirProntuarioCirurgia() throws ApplicationBusinessException {

		List<ProntuarioCirurgiaVO> listaProntuariosSelecionados = new ArrayList<ProntuarioCirurgiaVO>();

		if (listaCirurgiaVO == null) {
			this.apresentarMsgNegocio(Severity.INFO,MENSAGEM_NENHUMA_MOVIMENTACAO_SELECIONADA);
			this.reiniciar();
			return null;
		}
		for (ProntuarioCirurgiaVO voProntuario : listaCirurgiaVO) {
			if (voProntuario != null && voProntuario.getSelecionado()) {
				listaProntuariosSelecionados.add(voProntuario);
			}
		}

		if (listaProntuariosSelecionados.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_NENHUMA_MOVIMENTACAO_SELECIONADA);
			return null;
		}
		
		this.pacienteFacade.atualizarRegistrosDesarquivamentoProntuariosCirurgia(listaProntuariosSelecionados);

		reiniciar();

		this.relatorioDesarquivamentoProntuarioCirurgiaController.setAbaSelecionada(getAbaSelecionada());
		this.relatorioDesarquivamentoProntuarioCirurgiaController.setListaProntuarios(listaProntuariosSelecionados);
		
		String redirect = this.relatorioDesarquivamentoProntuarioCirurgiaController.print();
		listaCirurgiaVO = null;
		return redirect;
	}
	
	public void selecionarProntuarioAmbulatorio(AipMovimentacaoProntuarioVO prontuarioVO){
		if (listaMovimentacaoVO == null){
			listaMovimentacaoVO = new ArrayList<AipMovimentacaoProntuarioVO>();
		}
		if (listaMovimentacaoVO.contains(prontuarioVO)) {
			listaMovimentacaoVO.remove(prontuarioVO);
		} else {
			listaMovimentacaoVO.add(prontuarioVO);
		}
	}
	
	public void selecionarProntuarioInternacao(ProntuarioInternacaoVO internacaoVO){
		if (this.prontuariosInternacao == null){
			this.prontuariosInternacao = new ArrayList<ProntuarioInternacaoVO>();
		}
		if (this.prontuariosInternacao.contains(internacaoVO)) {
			this.prontuariosInternacao.remove(internacaoVO);
		} else {
			prontuariosInternacao.add(internacaoVO);
		}
	}
	
	public void selecionarProntuarioCirurgia(ProntuarioCirurgiaVO prontuarioVO){
		if (listaCirurgiaVO == null){
			listaCirurgiaVO = new ArrayList<ProntuarioCirurgiaVO>();
		}
		if (listaCirurgiaVO.contains(prontuarioVO)) {
			listaCirurgiaVO.remove(prontuarioVO);
		} else {
			listaCirurgiaVO.add(prontuarioVO);
		}
	}

	public Integer getAbaSelecionada() {
		return abaSelecionada;
	}

	public void setAbaSelecionada(Integer abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}

	public List<ProntuarioInternacaoVO> getProntuariosInternacao() {
		return prontuariosInternacao;
	}

	public void setProntuariosInternacao(
			List<ProntuarioInternacaoVO> prontuariosInternacao) {
		this.prontuariosInternacao = prontuariosInternacao;
	}

	public List<ProntuarioInternacaoVO> getListaInternacao() {
		return listaInternacao;
	}

	public void setListaInternacao(List<ProntuarioInternacaoVO> listaInternacao) {
		this.listaInternacao = listaInternacao;
	}
	
}
