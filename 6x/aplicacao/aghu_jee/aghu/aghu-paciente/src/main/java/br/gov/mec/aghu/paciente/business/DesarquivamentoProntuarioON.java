package br.gov.mec.aghu.paciente.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ProntuarioCirurgiaVO;
import br.gov.mec.aghu.internacao.vo.ProntuarioInternacaoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipAvisoAgendamentoCirurgia;
import br.gov.mec.aghu.model.AipFinalidadesMovimentacao;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipSolicitacaoProntuarios;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipAvisoAgendamentoCirurgiaDAO;
import br.gov.mec.aghu.paciente.dao.AipMovimentacaoProntuarioDAO;
import br.gov.mec.aghu.paciente.vo.DesarquivamentoProntuarioVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Classe responsável por prover os métodos de negócio genéricos para o
 * relatório de Desarquivamento de Prontuarios.
 * 
 * @author lalegre
 * 
 */
@Stateless
public class DesarquivamentoProntuarioON extends BaseBusiness {

	@EJB
	private MovimentacaoProntuarioRN movimentacaoProntuarioRN;
	
	private static final Log LOG = LogFactory.getLog(DesarquivamentoProntuarioON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private AipAvisoAgendamentoCirurgiaDAO aipAvisoAgendamentoCirurgiaDAO;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@Inject
	private AipMovimentacaoProntuarioDAO aipMovimentacaoProntuarioDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5476212487962905606L;

	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais e pesquisa
	 * solicitação de desarquivamento de prontuário.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum DesarquivamentoProntuarioONExceptionCode implements
			BusinessExceptionCode {
		FILTRO_OBRIGATORIO, DATA_GERACAO_MOVIMENTO_PRONTUARIO_NOT_FOUND, ERRO_PARAMETRO_IDADE_LIMITE
	}

	public List<DesarquivamentoProntuarioVO> pesquisa(Integer slpCodigo, Date dataMovimento) {
		List<Object[]> res = this.getAipMovimentacaoProntuarioDAO().listaInformacoesMovimentacoesProntuario(slpCodigo, dataMovimento);
		List<DesarquivamentoProntuarioVO> lista = new ArrayList<DesarquivamentoProntuarioVO>(0);

		// Criando lista de VO.
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Iterator<Object[]> it = res.iterator();
		while (it.hasNext()) {
			Object[] obj = it.next();
			DesarquivamentoProntuarioVO vo = new DesarquivamentoProntuarioVO();

			if (obj[0] != null) {
				vo.setSolicitacao(((Integer) obj[0]).toString());
			}

			if (obj[1] != null) {
				// Tranforma número de 124567 para 12456/7
				String prontAux = ((Integer) obj[1]).toString();
				vo.setProntuario(prontAux.substring(0, prontAux.length() - 1)
						+ "/" + prontAux.charAt(prontAux.length() - 1));
			}

			if (obj[2] != null) {
				vo.setNome((String) obj[2]);
			}
			
			if (StringUtils.isNotBlank((String) obj[4])) {
				vo.setLocal((String) obj[3] + " OBS: ");
				vo.setObservacao((String) obj[4]);
			} else {
				vo.setLocal((String) obj[3]);
			}

			if (obj[5] != null) {
				vo.setVolume(((Short) obj[5]).toString());
			}

			if (obj[6] != null) {
				vo.setDataMvto(sdf.format((Date) obj[6]));
			}

			lista.add(vo);
		}

		// Ordenação por seção do prontuário.
		// substr(to_char(PAC.PRONTUARIO,'09999999'),7,2)
		// Ex: Para o prontuário 123401/5 o que deve ser considerado para
		// ordenação é o número 01.
		Collections.sort(lista, new DesarquivamentoProntuarioComparator());

		return lista;
	}

	public void buscarProntuariosInternacao(List<ProntuarioInternacaoVO> listaProntuariosSelecionados, String nomeMicrocomputador, final Date dataFimVinculoServidor) {
		IInternacaoFacade internacaoFacade = this.getInternacaoFacade();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		for (ProntuarioInternacaoVO prontuarioVO : listaProntuariosSelecionados) {
			try {
				AinInternacao internacao = internacaoFacade.obterAinInternacaoPorChavePrimaria(prontuarioVO.getIdInternacao());
				internacao.setDataHoraProntuarioBuscado(new Date());

				internacaoFacade.atualizarInternacao(internacao, nomeMicrocomputador,servidorLogado, dataFimVinculoServidor, false, false);
			} catch (BaseException e) {
				logError("Exceção ignorada", e);
			}
		}

		this.flush();
	}

	/**
	 * Método que executa a ação de buscar nos prontuários a serem
	 * desarquivados.
	 * 
	 * @param movimentacoes
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	
	public AipSolicitacaoProntuarios buscarProntuarios(
			List<AipMovimentacaoProntuarios> movimentacoes)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
		
		Short idSamis;
		Integer idFinalidade;

		AipFinalidadesMovimentacao finalidadeMovimentacao;

		AipSolicitantesProntuario solicitanteSamis;
		
		try {
			AghParametros parametroUnidadeSamis = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_UNIDADE_SAMIS);

			idSamis = parametroUnidadeSamis.getVlrNumerico().shortValue();

			solicitanteSamis = this.getCadastrosBasicosPacienteFacade()
					.obterSolicitanteProntuario(idSamis);

		} catch (ApplicationBusinessException e) {
			logError("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw new ApplicationBusinessException(
					DesarquivamentoProntuarioONExceptionCode.DATA_GERACAO_MOVIMENTO_PRONTUARIO_NOT_FOUND);
		}
		AipSolicitacaoProntuarios solicitacao=null;
		try {
			AghParametros parametroFinalidade = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_FINALIDADE_CONS);

			idFinalidade = parametroFinalidade.getVlrNumerico().intValue();

			finalidadeMovimentacao = this.getCadastrosBasicosPacienteFacade()
					.obterFinalidadeMovimentacao(idFinalidade);

			solicitacao = new AipSolicitacaoProntuarios();
			solicitacao.setSolicitante("Controle SAMIS");
			solicitacao.setResponsavel(servidorLogado.getPessoaFisica().getNome());
			solicitacao.setAipSolicitantesProntuario(solicitanteSamis);
			solicitacao.setAipFinalidadesMovimentacao(finalidadeMovimentacao);
			this.getCadastroPacienteFacade().persistirSolicitacaoProntuario(solicitacao,
					new ArrayList<AipPacientes>());
			
		} catch (BaseException  e) {
			logError("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw new ApplicationBusinessException(
					DesarquivamentoProntuarioONExceptionCode.ERRO_PARAMETRO_IDADE_LIMITE);
		}	


		for (AipMovimentacaoProntuarios movimentacao : movimentacoes) {
			this.getMovimentacaoProntuarioRN().gerarJournalUpdate(movimentacao, servidorLogado.getUsuario() != null ? servidorLogado.getUsuario() : null);
			this.buscarMovimentacao(movimentacao);
		}
		
		this.flush();

		return solicitacao;
	}

	/**
	 * Método que executa a ação de buscar em um prontuário a ser desarquivado.
	 * @param movimentacao
	 * @throws ApplicationBusinessException
	 */
	private void buscarMovimentacao(AipMovimentacaoProntuarios movimentacao)throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		AipMovimentacaoProntuarios movimentacaoOld = replicarMovimentacaoProntuarios(movimentacao);
		if (movimentacao.getObservacoes() != null) {
			movimentacao.setSituacao(DominioSituacaoMovimentoProntuario.P);
		} else {
			movimentacao.setSituacao(DominioSituacaoMovimentoProntuario.R);
			String localAtual = movimentacao.getLocal().substring(movimentacao.getLocal().indexOf('/')+1, movimentacao.getLocal().lastIndexOf('/'));
			movimentacao.setLocalAtual(localAtual);			
		}

		this.getMovimentacaoProntuarioRN().atualizarDataDevolucaoProntuario(movimentacao, movimentacaoOld);

		AipPacientes aipPaciente = getPacienteFacade().obterAipPacientesPorChavePrimaria(movimentacao.getPaciente().getCodigo());
		if(aipPaciente != null && aipPaciente.getVolumes()==null){

			aipPaciente.setVolumes((short)0);

			this.getCadastroPacienteFacade().persistirPacienteSemValidacao(aipPaciente);

			movimentacao.setPaciente(aipPaciente);
		}
		
		this.getMovimentacaoProntuarioRN().atualizarMovimentacaoProntuario(movimentacao, movimentacaoOld, servidorLogado);
	}
	
	/**
	 * Replica o objeto AipMovimentacaoProntuarios
	 * @param movimentacaoOriginal
	 * @return
	 */
	public AipMovimentacaoProntuarios replicarMovimentacaoProntuarios(AipMovimentacaoProntuarios movimentacaoOriginal){
		
		AipMovimentacaoProntuarios movimentacaoOld = new AipMovimentacaoProntuarios();
		movimentacaoOld.setSeq(movimentacaoOriginal.getSeq());
		movimentacaoOld.setObservacoes(movimentacaoOriginal.getObservacoes());
		movimentacaoOld.setVolumes(movimentacaoOriginal.getVolumes());
		movimentacaoOld.setTipoEnvio(movimentacaoOriginal.getTipoEnvio());
		movimentacaoOld.setSituacao(movimentacaoOriginal.getSituacao());
		movimentacaoOld.setDataMovimento(movimentacaoOriginal.getDataMovimento());
		movimentacaoOld.setDataRetirada(movimentacaoOriginal.getDataRetirada());
		movimentacaoOld.setDataDevolucao(movimentacaoOriginal.getDataDevolucao());
		movimentacaoOld.setServidor(movimentacaoOriginal.getServidor());
		movimentacaoOld.setServidorRetirado(movimentacaoOriginal.getServidorRetirado());
		movimentacaoOld.setSolicitante(movimentacaoOriginal.getSolicitante());
		movimentacaoOld.setSolicitacao(movimentacaoOriginal.getSolicitacao());
		movimentacaoOld.setLocal(movimentacaoOriginal.getLocal());
		movimentacaoOld.setCriadoEm(movimentacaoOriginal.getCriadoEm());
		movimentacaoOld.setDataCadastroOrigemProntuario(movimentacaoOriginal.getDataCadastroOrigemProntuario());
		movimentacaoOld.setPaciente(movimentacaoOriginal.getPaciente());
		
		return movimentacaoOld;

	}

	/**
	 * método que lista prontuários a serem desarquivados
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<AipMovimentacaoProntuarios> pesquisarDesarquivamentoProntuarios(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return this.getAipMovimentacaoProntuarioDAO().pesquisarDesarquivamentoProntuarios(firstResult, maxResult, orderProperty, asc);
	}

	public Long obterCountPesquisaDesarquivamentoProntuarios() {
		return this.getAipMovimentacaoProntuarioDAO().obterCountPesquisaDesarquivamentoProntuarios();
	}

	public Long pesquisarAvisoInternacaoSAMESCount() {

		Long vlr = this.getInternacaoFacade().pesquisarInternacoesAvisoSamisUnidadeInternacaoCount();

		Long vlr1 = this.getInternacaoFacade().pesquisarInternacoesAvisoSamisUnidadeLeitoCount();
		
		Long vlr2 = this.getInternacaoFacade().pesquisarInternacoesAvisoSamisUnidadeQuartoCount();
		
		Long result = 0L;

		if(vlr != null){
			result += vlr;
		}
		
		if(vlr1 != null){
			result += vlr1;
		}
		
		if(vlr2 != null){
			result += vlr2;
		}
		
		return result;
	}
	
	public List<ProntuarioInternacaoVO> pesquisarAvisoInternacaoSAMES() {

		List<ProntuarioInternacaoVO> listaRetorno = new ArrayList<ProntuarioInternacaoVO>();

		listaRetorno.addAll(pesquisarInternacoesAvisoSamisUnidadeInternacao());
		
		if(listaRetorno.size()< 100){
			listaRetorno.addAll(pesquisarInternacoesAvisoSamisUnidadeLeito());
		}

		if(listaRetorno.size()< 100){
			listaRetorno.addAll(pesquisarInternacoesAvisoSamisUnidadeQuarto());
		}
		
		if(listaRetorno.size() > 100){
			listaRetorno = listaRetorno.subList(0, 100);
		}

		return listaRetorno;
	}

	private List<ProntuarioInternacaoVO> pesquisarInternacoesAvisoSamisUnidadeInternacao() {
		List<Object[]> retornoPesquisa = this.getInternacaoFacade().pesquisarInternacoesAvisoSamisUnidadeInternacao();

		List<ProntuarioInternacaoVO> listaRetorno = new ArrayList<ProntuarioInternacaoVO>();

		for (Object[] linha : retornoPesquisa) {
			listaRetorno.add(new ProntuarioInternacaoVO(linha));
		}
		
		return listaRetorno;
	}

	private List<ProntuarioInternacaoVO> pesquisarInternacoesAvisoSamisUnidadeLeito() {
		List<Object[]> retornoPesquisa = this.getInternacaoFacade().pesquisarInternacoesAvisoSamisUnidadeLeito();

		List<ProntuarioInternacaoVO> listaRetorno = new ArrayList<ProntuarioInternacaoVO>();

		for (Object[] linha : retornoPesquisa) {
			listaRetorno.add(new ProntuarioInternacaoVO(linha));

		}

		return listaRetorno;
	}

	private List<ProntuarioInternacaoVO> pesquisarInternacoesAvisoSamisUnidadeQuarto() {
		List<Object[]> retornoPesquisa = this.getInternacaoFacade().pesquisarInternacoesAvisoSamisUnidadeQuarto();

		List<ProntuarioInternacaoVO> listaRetorno = new ArrayList<ProntuarioInternacaoVO>();

		for (Object[] linha : retornoPesquisa) {
			listaRetorno.add(new ProntuarioInternacaoVO(linha));

		}

		return listaRetorno;
	}
	
	public List<ProntuarioCirurgiaVO> pesquisarDesarquivamentoProntuariosCirurgia(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return this.getAipAvisoAgendamentoCirurgiaDAO().pesquisarDesarquivamentoProntuariosCirurgia(firstResult, maxResult, orderProperty, asc);
	}
	
	public Long obterCountPesquisaDesarquivamentoProntuariosCirurgia() {
		return this.getAipAvisoAgendamentoCirurgiaDAO().obterCountPesquisaDesarquivamentoProntuariosCirurgia();
	}
	
	public void atualizarRegistrosDesarquivamentoProntuariosCirurgia(List<ProntuarioCirurgiaVO> listaProntuariosSelecionados) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		for (ProntuarioCirurgiaVO prontuarioCirurgia : listaProntuariosSelecionados) {
			AipAvisoAgendamentoCirurgia avisoAgendamentoCirurgia = this
				.getAipAvisoAgendamentoCirurgiaDAO().obterPorChavePrimaria(prontuarioCirurgia.getSeq());
			
			avisoAgendamentoCirurgia.setStatus("1");
			avisoAgendamentoCirurgia.setDthrRecebido(new Date());
			avisoAgendamentoCirurgia.setRapServidoresRecebido(servidorLogado);
		}
	}
	
	protected ICadastrosBasicosPacienteFacade getCadastrosBasicosPacienteFacade() {
		return (ICadastrosBasicosPacienteFacade) cadastrosBasicosPacienteFacade;
	}

	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return (ICadastroPacienteFacade) cadastroPacienteFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return (IPacienteFacade) pacienteFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return (IParametroFacade) parametroFacade;
	}

	protected MovimentacaoProntuarioRN getMovimentacaoProntuarioRN() {
		return movimentacaoProntuarioRN;
	}

	protected AipMovimentacaoProntuarioDAO getAipMovimentacaoProntuarioDAO() {
		return aipMovimentacaoProntuarioDAO;
	}
	
	protected AipAvisoAgendamentoCirurgiaDAO getAipAvisoAgendamentoCirurgiaDAO() {
		return aipAvisoAgendamentoCirurgiaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}

/**
 * Classe comparadora utilizada para ordenar a lista de
 * <code>DesarquivamentoProntuarioVO</code> pela seção do prontuário, que é
 * definida por dois dígitos no numero do prontuário. Ex: Um prontuário como
 * número 123401/5 possui o número do seção 01, representado pelo 5 e 6 dígito.
 * E os itens tem vir ordenados por esse campo.
 * 
 * @author lalegre
 * 
 */
class DesarquivamentoProntuarioComparator implements
		Comparator<DesarquivamentoProntuarioVO> {

	@Override
	public int compare(DesarquivamentoProntuarioVO o1,
			DesarquivamentoProntuarioVO o2) {

		// Exemplo: o1 = 388208/3 e o2 = 233208/8
		String vo1Desc = ((DesarquivamentoProntuarioVO) o1).getProntuario();
		String vo2Desc = ((DesarquivamentoProntuarioVO) o2).getProntuario();

		int solicitacao1 = Integer.parseInt(((DesarquivamentoProntuarioVO) o1)
				.getSolicitacao());
		int solicitacao2 = Integer.parseInt(((DesarquivamentoProntuarioVO) o2)
				.getSolicitacao());

		// Exemplo: secao1 = 08 e secao2 = 08
		int secao1 = Integer.parseInt(vo1Desc.substring(vo1Desc.length() - 4,
				vo1Desc.length() - 2));

		int secao2 = Integer.parseInt(vo2Desc.substring(vo2Desc.length() - 4,
				vo2Desc.length() - 2));

		int pre1 = 0;
		int pre2 = 0;

		// Exemplo: secao1 = 3882 e secao2 = 2332
		if (vo1Desc.length() > 4) {
			pre1 = Integer.parseInt(vo1Desc.substring(0, vo1Desc.length() - 4));
		}

		if (vo2Desc.length() > 4) {
			pre2 = Integer.parseInt(vo2Desc.substring(0, vo2Desc.length() - 4));
		}

		if (solicitacao1 > solicitacao2) {
			return 1;
		} else if (solicitacao1 < solicitacao2) {
			return -1;
		} else {
			if (secao1 > secao2) {
				return 1;
			} else if (secao1 < secao2) {
				return -1;
			} else {
				if (pre1 > pre2) {
					return 1;
				} else if (pre1 < pre2) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}
		
}
