package br.gov.mec.aghu.sig.custos.business;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICentralPendenciaFacade;
import br.gov.mec.aghu.dominio.DominioTipoEventoComunicacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigProcessamentoAnalises;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sig.custos.vo.ProcessamentoCustoFinalizadoVO;
import br.gov.mec.aghu.sig.dao.SigComunicacaoEventosDAO;
import br.gov.mec.aghu.sig.dao.SigMvtoContaMensalDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoAnalisesDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoCustoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class IntegracaoCentralPendenciasON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(IntegracaoCentralPendenciasON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SigMvtoContaMensalDAO sigMvtoContaMensalDAO;
	
	@EJB
	private ICentralPendenciaFacade centralPendenciaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SigProcessamentoAnalisesDAO sigProcessamentoAnalisesDAO;
	
	@Inject
	private SigComunicacaoEventosDAO sigComunicacaoEventosDAO;
	
	@Inject
	private SigProcessamentoCustoDAO sigProcessamentoCustoDAO;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	private static final long serialVersionUID = -1968768799315215L;
	
	public void adicionarPendenciaCadastroNovoExame(String nomeObjetoCusto, Integer seqObjetoCustoVersao, Integer codigoCentroCusto) throws ApplicationBusinessException{
		String mensagem = this.buscarMensagem("MENSAGEM_CAIXA_POSTAL_CADASTRO_NOVO_EXAME", nomeObjetoCusto);
		String url = buscarUrl(AghuParametrosEnum.P_URL_PENDENCIA_PAGINA_MANTER_OBJETO_CUSTO) + seqObjetoCustoVersao;
		String descricaoAba = buscarMensagem("TEXTO_ABA_OBJETO_CUSTO_CADASTRADO");
		this.adicionarPendencia(DominioTipoEventoComunicacao.CE, mensagem, url, descricaoAba, codigoCentroCusto);
	}
	
	public void adicionarPendenciaProcessamentoFinalizado(ProcessamentoCustoFinalizadoVO vo)throws ApplicationBusinessException {
		DominioTipoEventoComunicacao tipoEvento = null;
		String mensagem = "";
		String descricaoAba = "";
		String url = "";
		if(vo.isOcorreuErroProcessamento()){
			tipoEvento = DominioTipoEventoComunicacao.PE;
			mensagem = buscarMensagem("MENSAGEM_CAIXA_POSTAL_PROCESSAMENTO_FINALIZADO_ERRO",vo.getCompetencia());
			descricaoAba =  buscarMensagem("TEXTO_ABA_CENTRAL_PENDENCIA_ERRO_PROCESSAMENTO");
			url = buscarUrl(AghuParametrosEnum.P_URL_PENDENCIA_PAGINA_VISUALIZAR_HISTORICO_CUSTO) + vo.getSeqProcessamentoCusto();
		}
		else{
			tipoEvento = DominioTipoEventoComunicacao.PF;
			mensagem = buscarMensagem("MENSAGEM_CAIXA_POSTAL_PROCESSAMENTO_FINALIZADO_SUCESSO", vo.getCompetencia());
			descricaoAba = buscarMensagem("TEXTO_ABA_CENTRAL_PENDENCIA_SUCESSO_PROCESSAMENTO");
			url = buscarUrl(AghuParametrosEnum.P_URL_PENDENCIA_PAGINA_PROCESSAMENTO_MENSAL_CRUD) + vo.getSeqProcessamentoCusto();
		}
		this.adicionarPendencia(tipoEvento, mensagem, url, descricaoAba, null);
	}
	
	public void adicionarPendenciaProcessamentoHomologado(Integer seqProcessamentoCusto) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		SigProcessamentoCusto processamentoCusto = this.getSigProcessamentoCustoDAO().obterPorChavePrimaria(seqProcessamentoCusto);
		List<Integer> consultaCentrosCustosComProcessamento = this.getSigMvtoContaMensalDAO().consultarCentrosCustosComProcessamento(seqProcessamentoCusto);

		SigProcessamentoAnalisesDAO sigProcessamentoAnalisesDAO = this.getSigProcessamentoAnalisesDAO();
		for (Integer integer : consultaCentrosCustosComProcessamento) {
			FccCentroCustos fccCentroCustos = this.getCentroCustoFacade().obterCentroCustoPorChavePrimaria(integer);

			SigProcessamentoAnalises sigProcessamentoAnalises = new SigProcessamentoAnalises();
			sigProcessamentoAnalises.setSigProcessamentoCustos(processamentoCusto);
			sigProcessamentoAnalises.setFccCentroCustos(fccCentroCustos);
			sigProcessamentoAnalises.setCriadoEm(new Date());
			sigProcessamentoAnalises.setRapServidores(servidorLogado);
			sigProcessamentoAnalisesDAO.persistir(sigProcessamentoAnalises);

			RapServidores buscarUsuarioResponsavelCentroCusto = this.getSigComunicacaoEventosDAO().buscarUsuarioResponsavelCentroCusto(integer);

			if (buscarUsuarioResponsavelCentroCusto != null && buscarUsuarioResponsavelCentroCusto.getUsuario() != null) {

				//FIXME: Transformar o link para parâmetro (AGH_PARAMETROS)
				this.getCentralPendenciaFacade().adicionarPendenciaAcao(
						buscarMensagem("MENSAGEM_CAIXA_POSTAL_PROCESSAMENTO_HOMOLOGADO", processamentoCusto.getCompetenciaMesAno()),
						"/sig/custos/processamentoMensalAnaliseParecer.seam?seqProcessamentoCusto=" + processamentoCusto.getSeq()
								+ "&seqProcessamentoAnalise=" + sigProcessamentoAnalises.getSeq(), buscarMensagem("TEXTO_ABA_CENTRAL_PENDENCIA_HOMOLOGADO"),
						buscarUsuarioResponsavelCentroCusto, false);
			}
		}
	}
	
	private void adicionarPendencia(DominioTipoEventoComunicacao tipoEvento, String mensagem, String url, String descricaoAba, Integer codigoCentroCusto) throws ApplicationBusinessException {
		List<RapServidores> listaServidores = this.getSigComunicacaoEventosDAO().listarUsuariosNotificaveis(tipoEvento, codigoCentroCusto);
		this.getCentralPendenciaFacade().adicionarPendenciaAcao(mensagem, url, descricaoAba, listaServidores, true);
	}
	
	private String buscarMensagem(String chave, Object... parametros) {
		String valorChave =  getResourceBundleValue(chave);
		MessageFormat mensagemFormatada = new MessageFormat(valorChave);
		return mensagemFormatada.format(parametros);
	}
	
	private String buscarUrl(AghuParametrosEnum parametro) throws ApplicationBusinessException{
		AghParametros parametroUrl = getParametroFacade().buscarAghParametro(parametro);
		return parametroUrl.getVlrTexto();
	}
		
	protected SigComunicacaoEventosDAO getSigComunicacaoEventosDAO(){
		return sigComunicacaoEventosDAO;
	}
	
	protected SigProcessamentoCustoDAO getSigProcessamentoCustoDAO(){
		return sigProcessamentoCustoDAO;
	}
	
	protected SigMvtoContaMensalDAO getSigMvtoContaMensalDAO(){
		return sigMvtoContaMensalDAO;
	}	
	
	protected SigProcessamentoAnalisesDAO getSigProcessamentoAnalisesDAO(){
		return sigProcessamentoAnalisesDAO;
	}	
	
	protected ICentralPendenciaFacade getCentralPendenciaFacade(){
		return centralPendenciaFacade;
	}
	 
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
