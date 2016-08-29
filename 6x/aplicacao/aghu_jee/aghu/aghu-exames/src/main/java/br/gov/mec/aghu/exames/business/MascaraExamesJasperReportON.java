package br.gov.mec.aghu.exames.business;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioExibicaoParametroCamposLaudo;
import br.gov.mec.aghu.dominio.DominioFormaRespiracao;
import br.gov.mec.aghu.dominio.DominioObjetoVisual;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.dao.AelAtendimentoDiversosDAO;
import br.gov.mec.aghu.exames.dao.AelCampoVinculadoDAO;
import br.gov.mec.aghu.exames.dao.AelDescricoesResulPadraoDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoIDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameIDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameIDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoPadraoCampoDAO;
import br.gov.mec.aghu.exames.laudos.EnderecoContatosVO;
import br.gov.mec.aghu.exames.laudos.ExameVO;
import br.gov.mec.aghu.exames.laudos.ExamesListaVO;
import br.gov.mec.aghu.exames.laudos.JasperHtmlAdapter;
import br.gov.mec.aghu.exames.laudos.MascaraVO;
import br.gov.mec.aghu.exames.laudos.NotaAdicionalVO;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.vo.AelpCabecalhoLaudoVO;
import br.gov.mec.aghu.exames.vo.DataLiberacaoVO;
import br.gov.mec.aghu.exames.vo.DataRecebimentoSolicitacaoVO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelCampoVinculado;
import br.gov.mec.aghu.model.AelDescricoesResulPadrao;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelNotaAdicional;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.IAelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.IAelItemSolicitacaoExames;
import br.gov.mec.aghu.model.IAelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.IAelResultadoExame;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength" })
@Stateless
public class MascaraExamesJasperReportON extends BaseBusiness {

	private static final long serialVersionUID = 2831485648403262351L;
	
	private static final Log LOG = LogFactory.getLog(MascaraExamesJasperReportON.class);

	private static final String SEM_LEGENDA = "Sem legenda";
	
	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IExamesLaudosFacade examesLaudosFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;

	@Inject
	private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	@Inject
	private AelCampoVinculadoDAO aelCampoVinculadoDAO;

	@Inject
	private AelResultadoPadraoCampoDAO aelResultadoPadraoCampoDAO;

	@Inject
	private AelDescricoesResulPadraoDAO aelDescricoesResulPadraoDAO;

	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelItemSolicitacaoExameIDAO aelItemSolicitacaoExameIDAO;

	@Inject
	private AelResultadoExameIDAO aelResultadoExameIDAO;

	@Inject
	private AelExtratoItemSolicitacaoIDAO aelExtratoItemSolicitacaoIDAO;

	@EJB
	private InformacoesColetaON informacoesColetaON;

	@EJB
	private MascaraExamesRN mascaraExamesRN;
	
	@Inject
	private AelAtendimentoDiversosDAO aelAtendimentoDiversosDAO;

	/**
	 * Retorna Map onde a chave são os parâmetros do campo laudo associado ao
	 * resultado do exames e o valor o resultado.
	 * 
	 * @param item
	 * @return
	 */
	private List<IAelResultadoExame> obterMapaResultadosExames(
			IAelItemSolicitacaoExames item) {

		return this.getAelResultadoExameIDAO().listarResultadosExame(item);
	}

	private List<AelParametroCamposLaudo> obterListaCamposExibicao(
			IAelItemSolicitacaoExames item,
			Map<AelParametroCamposLaudo, IAelResultadoExame> resultados,
			List<AelParametroCamposLaudo> camposDaVersao) {
		List<AelParametroCamposLaudo> result = new ArrayList<AelParametroCamposLaudo>();
		if (camposDaVersao == null) {
			return result;
		}
		AelParametroCamposLaudo paramCampoLaudo = resultados.entrySet().iterator().next().getKey();
		
		List<AelCampoVinculado> listaCamposVinculadores = getAelCampoVinculadoDAO()
				.pesquisarCampoVinculadoPorParametroCampoLaudo(paramCampoLaudo.getId().getVelEmaExaSigla(), paramCampoLaudo.getId().getVelEmaManSeq(), paramCampoLaudo.getId().getVelSeqp());

		for (AelParametroCamposLaudo aelParametroCampoLaudo : camposDaVersao) {

			/* Pega todos os campos que vincularam o campo */
			List<AelCampoVinculado> camposVinculadores = getCamposVinculadores(listaCamposVinculadores, aelParametroCampoLaudo);

			IAelResultadoExame lista = resultados.get(aelParametroCampoLaudo);
			
			boolean hasResults = lista != null;

			if (camposVinculadores != null && !camposVinculadores.isEmpty()
					&& !hasResults) {
				boolean algumCampoVincula = false;
				/*
				 * Verifica se os campos que vincularam o campo tem resultados
				 * buscando map de resultados
				 */
				for (AelCampoVinculado campoVinculado : camposVinculadores) {
					// Pega do map
					algumCampoVincula = this.processarResultadoEDescricaoExame(
							resultados, campoVinculado);

					if (algumCampoVincula) {
						result.add(aelParametroCampoLaudo);
						break;
					}
				}
			} else {
				result.add(aelParametroCampoLaudo);
			}
		}
		return result;
	}
	
	private List<AelCampoVinculado> getCamposVinculadores(List<AelCampoVinculado> listaCamposVinculadores,
			AelParametroCamposLaudo aelParametroCampoLaudo) {
		
		List<AelCampoVinculado> listaRetorno = new ArrayList<AelCampoVinculado>();
		
		for (AelCampoVinculado campoVinculado : listaCamposVinculadores) {
			if (campoVinculado.getAelParametroCamposLaudoByAelCvcPclFk2().equals(aelParametroCampoLaudo)) {
				listaRetorno.add(campoVinculado);
			}
		}
		
		return listaRetorno;
	}

	private boolean processarResultadoEDescricaoExame(
			Map<AelParametroCamposLaudo, IAelResultadoExame> resultados,
			AelCampoVinculado campoVinculado) {

		IAelResultadoExame resultado = resultados.get(campoVinculado
				.getAelParametroCamposLaudoByAelCvcPclFk1());
		if (resultado != null
				&& (resultado.getValor() != null
						|| resultado.getDescricao() != null
						|| resultado.getResultadoCodificado() != null || resultado
						.getResultadoCaracteristica() != null)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public Map<AelParametroCamposLaudo, AelResultadoExame> obterMapaResultadosPadraoExames(
			AelResultadosPadrao resultPadrao) {
		List<AelResultadoPadraoCampo> resultadosDoExame = this
				.getAelResultadoPadraoCampoDAO()
				.pesquisarResultadoPadraoCampoPorResultadoPadraoSeq(
						resultPadrao.getSeq());
		Map<AelParametroCamposLaudo, AelResultadoExame> resultados = new HashMap<AelParametroCamposLaudo, AelResultadoExame>();

		for (AelResultadoPadraoCampo resultado : resultadosDoExame) {

			AelResultadoExame resultadoNormal = new AelResultadoExame();

			if (resultado != null
					&& (resultado.getResultadoCodificado() == null)) {

				AelDescricoesResulPadrao descricaoResultPadrao = getAelDescricoesResulPadraoDAO()
						.obterAelDescricoesResulPadraoPorID(
								resultado.getId().getRpaSeq(),
								resultado.getId().getSeqp());

				if (descricaoResultPadrao != null) {
					resultadoNormal.setDescricao(descricaoResultPadrao.getDescricao());
				}
			} else {
				resultadoNormal.setResultadoCodificado(resultado
						.getResultadoCodificado());
				resultadoNormal.setResultadoCaracteristica(resultado
						.getResultadoCaracteristica());
				resultadoNormal.setParametroCampoLaudo(resultado
						.getParametroCampoLaudo());
			}

			if (resultado.getValor() != null) {
				resultadoNormal.setValor(resultado.getValor());
			}

			resultados.put(resultado.getParametroCampoLaudo(), resultadoNormal);
		}

		return resultados;
	}

	/**
	 * Retorna VO com dados para geração do laudo.
	 * 
	 * @param item
	 * @return
	 * @throws AGHUNegocioExceptionSemRollback
	 * @throws MECBaseException
	 * @throws AGHUNegocioException
	 */
	private ExameVO processaItem(IAelItemSolicitacaoExames item)
			throws BaseException {
		if (item == null) {
			throw new IllegalArgumentException("O item não pode ser nulo.");
		}

		// TODO rever
		AelpCabecalhoLaudoVO cabecalho = this.criarCabecalho(item);

		ExameVO exame = new ExameVO();
		exame.setSolicitacao(item.getId().getSoeSeq());
		exame.setItem(item.getId().getSeqp());
		exame.setSigla(item.getExame().getSigla());
		exame.setDescricao(item.getExame().getDescricao());
		exame.setConvenio(cabecalho.getConvenioPaciente());
		this.populaMaterialAnaliseRegiaoAnatomica(item, exame);

		DominioOrigemAtendimento origem = pesquisaExamesFacade.validaLaudoOrigemPaciente(item.getSolicitacaoExame());
		if (origem != null) {
			exame.setOrigem(origem.getDescricao());
		} else {
			AelAtendimentoDiversos atv = aelAtendimentoDiversosDAO.obterAtendimentoDiversoPorSolicitacaoExame(item
					.getSolicitacaoExame().getSeq());
			if (atv != null) {
				exame.setAtdDiverso(true);
			}
		}

		// dados do serviço da unidade executora
		exame.setServico(cabecalho.getpDescServico()); // ok
		exame.setRegistroConselhoServico(cabecalho.getpRegistro()); // ok
		exame.setChefeServico(cabecalho.getpLinhaCab2()); // ok
		exame.setConselhoChefeServico(cabecalho.getpConselho()); // ok
		exame.setNroConselhoChefeServico(cabecalho.getpNroConselho()); // ok

		FccCentroCustos centroCustoServico = this.buscarCentroCustoServico(item
				.getAelUnfExecutaExames().getUnidadeFuncional());
		if (centroCustoServico != null) {
			String templateFone = "(55)(%1$s) %2$s";
			EnderecoContatosVO enderecoContatos = new EnderecoContatosVO();
			String cnpj = this.getParametroFacade().buscarValorTexto(
					AghuParametrosEnum.P_HOSPITAL_CGC);
			enderecoContatos.setCnpj(cnpj);
			if(centroCustoServico.getDddFax() != null && centroCustoServico.getFax() != null) {
				enderecoContatos
				.setFax(String.format(templateFone,
						centroCustoServico.getDddFax(),
						centroCustoServico.getFax()));
			}
			enderecoContatos.setFone(String.format(templateFone,
					centroCustoServico.getDddFone(),
					centroCustoServico.getFone()));
			enderecoContatos.setEmail(centroCustoServico.getEmail());
			enderecoContatos
					.setCaixaPostal(centroCustoServico.getCaixaPostal());
			enderecoContatos.setHomepage(centroCustoServico.getHomePage());
			enderecoContatos.setEndereco(cabecalho.getpLinhaCab4());
			exame.setEnderecoContatos(enderecoContatos);
		}

		// dados da unidade executora
		exame.setUnidade(cabecalho.getpDescUnidade()); // ok
		exame.setChefeUnidade(cabecalho.getpChefeUnidade()); // ok
		exame.setConselhoUnidade(cabecalho.getpConselhoUnid()); // ok
		exame.setNroConselhoUnidade(cabecalho.getpNroConselhoUnid()); // ok

		String assinaturaMedico = processaAssinaturaMedico(item);
		exame.setAssinaturaMedico(assinaturaMedico);
		
		String assinaturaEletronica = this.criarAssinaturaEletronica(item, exame);
		exame.setAssinaturaEletronica(assinaturaEletronica);

		String nomeMedicoSolicitante = this.buscaNomeMedicoSolicitante(item);
		exame.setNomeMedicoSolicitante(nomeMedicoSolicitante);

		exame.setNomePaciente(obterNomePaciente(item));
		
		if (item.getSolicitacaoExame().getAtendimento() != null) {
			exame.setProntuario(item.getSolicitacaoExame().getAtendimento().getProntuario());
		} else {
			String prontuario = examesFacade.buscarLaudoProntuarioPaciente(item.getSolicitacaoExame());
			if (StringUtils.isNotBlank(prontuario)){
				prontuario = prontuario.replace("/", "");
				exame.setProntuario(Integer.valueOf(prontuario));
			}
		}
		
		// notas adicionais
		this.populaNotasAdicionais(item, exame);

		// respiracao
		String informacaoRespiracao = this.criarInformacaoRespiracao(item);
		exame.setInformacoesRespiracao(informacaoRespiracao);

		// recebimento
		Date dataRecebimento = getAelExtratoItemSolicitacaoIDAO()
				.buscaMaiorDataRecebimento(item,
						DominioSituacaoItemSolicitacaoExame.AE);
		exame.setRecebimento(dataRecebimento);

		// liberacao
		if (dataRecebimento != null) {
			Date dataLiberacao = getExamesLaudosFacade()
					.buscaMaiorDataLiberacao(item.getId().getSoeSeq(),
							item.getId().getSeqp());
			exame.setLiberacao(dataLiberacao);
		}

		// informacoes coleta
		exame.setInformacoesColeta(this.getInformacoesColetaON()
				.criarInformacoesColeta(item));

		this.populaResultadosCamposLaudo(item, exame);

		return exame;

	}
	
	public String obterNomePaciente(IAelItemSolicitacaoExames itemSolicitacao){
		String nomePaciente;	
		
		if(itemSolicitacao.getSolicitacaoExame().getRecemNascido()){
			nomePaciente = "RN de " + getExamesFacade().buscarLaudoNomeMaeRecemNascido(itemSolicitacao.getSolicitacaoExame());
		}else{
			nomePaciente = getExamesFacade().buscarLaudoNomePaciente(itemSolicitacao.getSolicitacaoExame());
		}			
		
		return nomePaciente;
	}

	private String processaAssinaturaMedico(IAelItemSolicitacaoExames item) {
		String assinaturaMedico = "";
		RapServidores servidorResponsabilidade = item.getServidorResponsabilidade();
		AelUnfExecutaExames unfExecutaExame = item.getAelUnfExecutaExames();
		
		if(servidorResponsabilidade != null){

			Boolean uniFechada = (unfExecutaExame != null 
					&& unfExecutaExame.getUnidadeFuncional() != null 
					&& unfExecutaExame.getUnidadeFuncional().possuiCaracteristica(ConstanteAghCaractUnidFuncionais.AREA_FECHADA));

			//RapServidores servidor = itemSolicitacaoExame.getServidorResponsabilidade();

			List<ConselhoProfissionalServidorVO> conselhos = getRegistroColaboradorFacade()
					.buscaConselhosProfissionalServidorAtivoInativo(
							servidorResponsabilidade.getId().getMatricula(),
							servidorResponsabilidade.getId().getVinCodigo());

			if(uniFechada && conselhos.size() > 0){
				ConselhoProfissionalServidorVO conselho = conselhos.get(0);
				String titulo = "";
				if (DominioSexo.M.equals(conselho.getSexo())) {
					titulo = conselho.getTituloMasculino();
				}
				else {
					titulo = conselho.getTituloFeminino();
				}
				assinaturaMedico = (titulo != null ? titulo + " " : "") + conselho.getNome() + " - " + conselho.getSiglaConselho() + (conselho.getNumeroRegistroConselho() != null ? ": " + conselho.getNumeroRegistroConselho() : "");
			}
		}
		return assinaturaMedico;
	}

	/**
	 * Trata os resultados e campos do laudo populando os vo de exames.
	 * 
	 * @param item
	 * @param exame
	 * @throws MECBaseException
	 */
	private void populaResultadosCamposLaudo(IAelItemSolicitacaoExames item,
			ExameVO exame) throws BaseException {

		List<IAelResultadoExame> resultados = this
				.obterMapaResultadosExames(item);
		
		if (resultados != null && !resultados.isEmpty()){
			AelVersaoLaudo versaoLaudo = resultados.get(0).getParametroCampoLaudo().getAelVersaoLaudo(); 
			List<AelParametroCamposLaudo> camposDaVersaoLaudo = this
					.getAelParametroCamposLaudoDAO()
					.pesquisarCamposRelatorioPorVersaoLaudo(versaoLaudo);
	
			Map<AelParametroCamposLaudo, String> listaTemp = new HashMap<AelParametroCamposLaudo, String>();
			Map<AelParametroCamposLaudo, IAelResultadoExame> resultadosAux = new HashMap<AelParametroCamposLaudo, IAelResultadoExame>();
			
			boolean achouVlrRef = false;
			
			for (AelParametroCamposLaudo campo : camposDaVersaoLaudo) {
				if (DominioObjetoVisual.VALORES_REFERENCIA.equals(campo.getObjetoVisual())) {
					achouVlrRef = true;
				}
				listaTemp.put(campo, this.obterValor(item, resultados, campo));
				resultadosAux.put(campo, obterResultadoExamePeloParametroCampoLaudo(resultados, campo));
			}
	
			if (!achouVlrRef) {
				String vlrRef = buscaInformacaoValoresReferenciaAntigo(item, item.getId().getSoeSeq(), item.getId().getSeqp(), resultados.get(0).getParametroCampoLaudo());
				exame.setVlrRef(vlrRef);
			}
			
			Map<AelParametroCamposLaudo, String> listaFinal = new HashMap<AelParametroCamposLaudo, String>();
			
			List<AelParametroCamposLaudo> exibir = this.obterListaCamposExibicao(item, resultadosAux, camposDaVersaoLaudo);
			
			for (Map.Entry<AelParametroCamposLaudo, String> entry : listaTemp.entrySet()) {
				AelParametroCamposLaudo key = entry.getKey();
				if (exibir.contains(key) || obterResultadoExamePeloParametroCampoLaudo(resultados, key) != null) {
					listaFinal.put(key, entry.getValue());
				}
			}
			
			exame.setMostraDescricao(versaoLaudo.getImprimeNomeExame());
			populaCamposMascara(listaFinal, exame);
		}
	}
	
	private IAelResultadoExame obterResultadoExamePeloParametroCampoLaudo(List<IAelResultadoExame> resultados, AelParametroCamposLaudo parametro) {
		for(IAelResultadoExame resultado : resultados) {
			if(resultado.getParametroCampoLaudo().getId().equals(parametro.getId())) {
				return resultado;
			}
		}
		return null;
	}
	
	@SuppressWarnings({"PMD.MissingBreakInSwitch"})
	private String obterValor(
			IAelItemSolicitacaoExames itemSolicitacaoExame,
			List<IAelResultadoExame> resultados,
			AelParametroCamposLaudo parametro)
			throws BaseException {

		IAelResultadoExame resultado = obterResultadoExamePeloParametroCampoLaudo(resultados, parametro);
		
		switch (parametro.getObjetoVisual()) {
		case TEXTO_FIXO:
			String textoLivre = parametro.getTextoLivre();
			if (textoLivre != null) {
				textoLivre = CoreUtil.converterRTF2Text(textoLivre.trim());
			}
			return textoLivre;

		case TEXTO_ALFANUMERICO:
			String textoAlfanumerico = null;
			if (resultado != null) {
				if (resultado.getDescricao() != null) {
					textoAlfanumerico = resultado.getDescricao();
				}
			}

			return textoAlfanumerico;

		case TEXTO_NUMERICO_EXPRESSAO:
			Boolean negativo = false;
			String textoNumericoExpressao = null;
			String neg = "-";
			if (resultado != null) {
				Short decimais = parametro.getQuantidadeCasasDecimais() == null ? (short) 0
						: parametro.getQuantidadeCasasDecimais();

				String valor = resultado.getValor() == null ? null : resultado
						.getValor().toString();
				if (valor != null && decimais.intValue() != 0) {
					if(resultado.getValor() < 0){
						negativo = true;
						valor = StringUtils.replace(valor,"-","");
					}
					// exemplo: valor 35 decimais 2 = 0,35
					String aux = StringUtils.leftPad(valor, decimais + 1, "0");
					StringBuffer v = new StringBuffer(aux);
					v.insert(v.length() - decimais, ",");
					StringBuffer vs = new StringBuffer();
					vs = v;
					if(negativo){
						vs = new StringBuffer();
						vs.append(neg).append(v);
					}
					textoNumericoExpressao = vs.toString();
				} else {
					textoNumericoExpressao = valor;
				}
			}

			return textoNumericoExpressao;

		case TEXTO_LONGO:
			if (resultado == null || resultado.getDescricao() == null) {
				return null;
			}
			String texto = JasperHtmlAdapter.jasperHtmlAdapter(resultado.getDescricao());
			
			return texto;

		case TEXTO_CODIFICADO:
			String result = null;
			if (resultado != null) {
				if (parametro.getCampoLaudo().getGrupoResultadoCodificado() != null) {
					result = resultado.getResultadoCodificado() == null ? null
							: resultado.getResultadoCodificado().getDescricao();
				} else {
					result = resultado.getResultadoCaracteristica() == null ? null
							: resultado.getResultadoCaracteristica()
									.getDescricao();
				}
			}

			return result;

		case EQUIPAMENTO:
			return this.buscaInformacaoEquipamento(itemSolicitacaoExame, itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp());

		case METODO:
			return this.buscaInformacaoMetodo(itemSolicitacaoExame, itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp());
			
		case RECEBIMENTO:
			return this.buscaInformacaoRecebimento(itemSolicitacaoExame, itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp());

		case HISTORICO:
			// TODO avaliar como deve enviar ja que sao varios textos na lista
			return this.buscaInformacaoHistorico(itemSolicitacaoExame, itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp(), parametro);
			
		case VALORES_REFERENCIA:
			return this.buscaInformacaoValoresReferencia(itemSolicitacaoExame, itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp(), parametro);
		}

		return null;
	}
	
	private String buscaInformacaoEquipamento(IAelItemSolicitacaoExames itemSolicitacaoExame, Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) {
		if (itemSolicitacaoExame instanceof AelItemSolicitacaoExames) {
			return this.getMascaraExamesRN().buscaInformacaoEquipamento(
					itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp());
		} 
		return null;
	}
	
	private String buscaInformacaoMetodo(IAelItemSolicitacaoExames itemSolicitacaoExame, Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) throws BaseException {
		if (itemSolicitacaoExame instanceof AelItemSolicitacaoExames) {
			return this.getMascaraExamesRN().buscaInformacaoMetodo(
					itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp());
		} else {
			return this.getMascaraExamesRN().buscaInformacaoMetodoHist(
					itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp());
		}
	}
	
	private String buscaInformacaoRecebimento(IAelItemSolicitacaoExames itemSolicitacaoExame, Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) throws BaseException {
		if (itemSolicitacaoExame instanceof AelItemSolicitacaoExames) {
			return this.getMascaraExamesRN().buscaInformacaoRecebimento(
					itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp());
		} else {
			return this.getMascaraExamesRN().buscaInformacaoRecebimentoHist(
					itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp());
		}
	}
	
	private String buscaInformacaoHistorico(IAelItemSolicitacaoExames itemSolicitacaoExame, Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq,
			AelParametroCamposLaudo parametro) throws BaseException {
		if (itemSolicitacaoExame instanceof AelItemSolicitacaoExames) {
			List<String> historico = this.getMascaraExamesRN()
					.buscaInformacaoHistorico(
							itemSolicitacaoExame.getId().getSoeSeq(),
							itemSolicitacaoExame.getId().getSeqp(),
							(parametro.getCampoLaudoRelacionado() == null ? null : parametro.getCampoLaudoRelacionado().getSeq()));
			if (historico != null && !historico.isEmpty()){
				return historico.toString();
			}
		} else {
			List<String> historico = this.getMascaraExamesRN()
					.buscaInformacaoHistoricoHist(
							itemSolicitacaoExame.getId().getSoeSeq(),
							itemSolicitacaoExame.getId().getSeqp(),
							(parametro.getCampoLaudoRelacionado() == null ? null : parametro.getCampoLaudoRelacionado().getSeq()));
			if (historico != null && !historico.isEmpty()){
				return historico.toString();
			}
		}
		return "";
	}
	
	private String buscaInformacaoValoresReferencia(IAelItemSolicitacaoExames itemSolicitacaoExame, Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq,
			AelParametroCamposLaudo parametro) throws BaseException {
		if (itemSolicitacaoExame instanceof AelItemSolicitacaoExames) {
			String valoresReferencia = this.getMascaraExamesRN()
					.buscaInformacaoValoresReferencia(
							itemSolicitacaoExame.getId().getSoeSeq(),
							itemSolicitacaoExame.getId().getSeqp(), parametro);
			if (parametro.getTextoLivre() != null) {
				valoresReferencia = valoresReferencia.replace(
						SEM_LEGENDA, "");
			}
			return valoresReferencia;
		} else {
			String valoresReferencia = this.getMascaraExamesRN()
					.buscaInformacaoValoresReferenciaHist(
							itemSolicitacaoExame.getId().getSoeSeq(),
							itemSolicitacaoExame.getId().getSeqp(), parametro);
			if (parametro.getTextoLivre() != null) {
				valoresReferencia = valoresReferencia.replace(
						SEM_LEGENDA, "");
			}
			return valoresReferencia;
		}
	}
	
	private String buscaInformacaoValoresReferenciaAntigo(IAelItemSolicitacaoExames itemSolicitacaoExame,
			Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq, AelParametroCamposLaudo parametro)
					throws BaseException {
		String valoresReferencia = this.getMascaraExamesRN().buscaInformacaoValoresReferenciaModeloAntigo(
				itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp(), parametro);
		if (parametro.getTextoLivre() != null) {
			valoresReferencia = valoresReferencia.replace(SEM_LEGENDA, "");
		}
		return valoresReferencia;
	}

	/**
	 * Popula os vos com os campos da mascara.
	 * 
	 * @param listaFinal
	 * @param exame
	 */
	private void populaCamposMascara(
			Map<AelParametroCamposLaudo, String> listaFinal, ExameVO exame) {
		// cria os VOs com os campos da mascara
		List<MascaraVO> mascaras = new ArrayList<MascaraVO>();
		for (Map.Entry<AelParametroCamposLaudo, String> entry : listaFinal
				.entrySet()) {
			AelParametroCamposLaudo key = entry.getKey();

			MascaraVO.IdVO id = new MascaraVO.IdVO(key.getId()
					.getVelEmaExaSigla(), key.getId().getVelEmaManSeq(), key
					.getId().getVelSeqp(), key.getId().getCalSeq(), key.getId()
					.getSeqp());
			MascaraVO mascara = new MascaraVO(id);

			if(key != null && key.getAlinhamento() != null){
				mascara.setAlinhamento(key.getAlinhamento().name().toString()
						.toCharArray()[0]);
			}
			mascara.setAlturaObjetoVisual(key.getAlturaObjetoVisual());
			mascara.setCor(key.getCor());
			// exibir no relatorio
			boolean exibir1 = false;
			if (key.getExibicao().equals(DominioExibicaoParametroCamposLaudo.R)
					|| key.getExibicao().equals(
							DominioExibicaoParametroCamposLaudo.A)) {
				exibir1 = true;
			}
			mascara.setExibeRelatorio(exibir1);
			// exibir na tela
			if (key.getExibicao().equals(DominioExibicaoParametroCamposLaudo.T)
					|| key.getExibicao().equals(
							DominioExibicaoParametroCamposLaudo.A)) {
				exibir1 = true;
			}
			mascara.setExibeTela(exibir1);

			mascara.setFonte(key.getFonte());
			mascara.setItalico(key.getItalico());
			mascara.setLarguraObjetoVisual(key.getLarguraObjetoVisual());
			mascara.setNegrito(key.getNegrito());
			mascara.setPosicaoColunaImpressao(key.getPosicaoColunaImpressao());
			mascara.setPosicaoLinhaImpressao(key.getPosicaoLinhaImpressao());
			mascara.setQuantidadeCaracteres(key.getQuantidadeCaracteres());
			mascara.setRiscado(key.getRiscado());
			mascara.setSublinhado(key.getSublinhado());
			mascara.setTamanhoFonte(key.getTamanhoFonte().intValue());
			mascara.setValor(entry.getValue());

			mascaras.add(mascara);
		}

		exame.setMascaras(mascaras);
	}

	/**
	 * Popula os vos com as notas adicionais.
	 * 
	 * @param item
	 * @param exame
	 */
	private void populaNotasAdicionais(IAelItemSolicitacaoExames item,
			ExameVO exame) {
		List<AelNotaAdicional> notas = this.getExamesFacade()
				.obterListaNotasAdicionaisPeloItemSolicitacaoExame(
						item.getId().getSoeSeq(), item.getId().getSeqp());

		List<NotaAdicionalVO> notasvo = new ArrayList<NotaAdicionalVO>();
		for (AelNotaAdicional nota : notas) {
			NotaAdicionalVO vo = new NotaAdicionalVO();
			vo.setNota(nota.getNotasAdicionais());
			vo.setCriadoEm(nota.getCriadoEm());
			vo.setCriadoPor(nota.getServidor().getPessoaFisica().getNome());
			notasvo.add(vo);
		}
		exame.setNotas(notasvo);
	}

	/**
	 * Retorna o centro de custo do serviço da unidade fornecida.
	 * 
	 * @param unidadeFuncional
	 * @return
	 */
	private FccCentroCustos buscarCentroCustoServico(
			AghUnidadesFuncionais unidadeFuncional) {
		FccCentroCustos result = null;

		// unidade pai
		AghUnidadesFuncionais unidPai = unidadeFuncional.getUnfSeq();

		// busca o cct do serviço
		if (unidPai == null || unidPai.getUnfSeq() == null) {
			result = unidadeFuncional.getCentroCusto();
		} else {
			result = unidadeFuncional.getCentroCusto().getCentroCusto();
		}

		return result;

	}

	/**
	 * Popula dados de endereço e contatos no bean fornecido.
	 * 
	 * @param exames
	 */
	private void populaEnderecoContatos(ExamesListaVO exames) {

		String templateEndereco = "%1$s - %2$s";
		String templateCnpjContatos = "CNPJ %1$S - Telefone %2$s - Fax %3$s - Caixa Postal %4$s";

		try {
			String endLinha1 = this.getParametroFacade().buscarValorTexto(
					AghuParametrosEnum.P_HOSPITAL_END_COMPLETO_LINHA1);
			String endLinha2 = this.getParametroFacade().buscarValorTexto(
					AghuParametrosEnum.P_HOSPITAL_END_COMPLETO_LINHA2);
			String cnpj = this.getParametroFacade().buscarValorTexto(
					AghuParametrosEnum.P_HOSPITAL_CGC);
			String fone = this.getParametroFacade().buscarValorTexto(
					AghuParametrosEnum.P_HOSPITAL_END_FONE);
			String fax = this.getParametroFacade().buscarValorTexto(
					AghuParametrosEnum.P_AGHU_HOSPITAL_FAX);
			String cxPostal = this.getParametroFacade().buscarValorTexto(
					AghuParametrosEnum.P_CX_POSTAL);
			String email = this.getParametroFacade().buscarValorTexto(
					AghuParametrosEnum.P_HOSPITAL_EMAIL);
			String homepage = this.getParametroFacade().buscarValorTexto(
					AghuParametrosEnum.P_HOSPITAL_SITE);
			String cnes = "2237601"; // TODO pegar de parametro

			EnderecoContatosVO enderecoContatos = new EnderecoContatosVO();
			exames.setEnderecoContatos(enderecoContatos);

			enderecoContatos.setCnes(cnes);

			String l1 = String.format(templateEndereco, endLinha1, endLinha2);
			enderecoContatos.setEndereco(l1);
			enderecoContatos.setCnpj(cnpj);

			String l2 = String.format(templateCnpjContatos, cnpj, fone, fax,
					cxPostal);
			enderecoContatos.setContatos(l2);
			enderecoContatos.setEmail(email);
			enderecoContatos.setHomepage(homepage);

		} catch (ApplicationBusinessException e) {
			LOG.warn("Erro ao buscar dados de endereço e contatos do hospital."
					+ "Relatórios de exames. " + e.getLocalizedMessage());
		}

	}

	protected String criarInformacaoRespiracao(IAelItemSolicitacaoExames item)
			throws BaseException {
		String result = null;

		DominioFormaRespiracao formaRespiracao = item.getFormaRespiracao();
		BigDecimal litrosOxigenio = item.getLitrosOxigenio();
		Short percOxigenio = item.getPercOxigenio();

		if (formaRespiracao != null) {
			if (formaRespiracao.equals(DominioFormaRespiracao.UM)) {
				result = formaRespiracao.getDescricao();
			} else if (formaRespiracao.equals(DominioFormaRespiracao.DOIS)) {
				result = formaRespiracao.getDescricao().replace("_",
						litrosOxigenio.toString());
			} else if (formaRespiracao.equals(DominioFormaRespiracao.TRES)) {
				result = formaRespiracao.getDescricao().replace("_",
						percOxigenio.toString());
			}
		}

		return result;
	}

	protected String criarAssinaturaEletronica(IAelItemSolicitacaoExames item,
			ExameVO exame) throws BaseException {
		boolean uniFechada = item.getAelUnfExecutaExames().getUnidadeFuncional()
				.possuiCaracteristica(ConstanteAghCaractUnidFuncionais.AREA_FECHADA);
		if (!uniFechada){
			StringBuffer result = new StringBuffer(50);
	
			AelUnfExecutaExames unfExecutaExames = item.getAelUnfExecutaExames();
			Integer matriculaConselho = null;
			Short vinCodigoConselho = null;
	
			IAelExtratoItemSolicitacao extrato = getAelExtratoItemSolicitacaoIDAO()
					.obterUltimoItemSolicitacaoSitCodigo(item,
							DominioSituacaoItemSolicitacaoExame.LI);
			if (extrato == null) {
				return null;
			} else {
				if (extrato.getServidorEhResponsabilide() != null) {
					matriculaConselho = extrato.getServidorEhResponsabilide()
							.getId().getMatricula();
					vinCodigoConselho = extrato.getServidorEhResponsabilide()
							.getId().getVinCodigo();
				} else {
					matriculaConselho = extrato.getServidor().getId()
							.getMatricula();
					vinCodigoConselho = extrato.getServidor().getId()
							.getVinCodigo();
				}
			}
	
			StringBuffer assinatura = new StringBuffer();
	
			boolean chefiaAssEletro = unfExecutaExames.getUnidadeFuncional()
					.possuiCaracteristica(
							ConstanteAghCaractUnidFuncionais.CHEFIA_ASS_ELET);
	
			if (chefiaAssEletro) {
				if (exame.getChefeUnidade() != null) {
					assinatura
							.append(exame.getChefeUnidade())
							.append(" - ")
							.append((exame.getConselhoUnidade() != null ? exame
									.getConselhoUnidade() + ": " : ""))
							.append((exame.getNroConselhoUnidade() != null ? exame
									.getNroConselhoUnidade() : ""));
				}
	
			} else {
				List<ConselhoProfissionalServidorVO> conselhos = null;
				conselhos = getRegistroColaboradorFacade()
						.buscaConselhosProfissionalServidorAtivoInativo(
								matriculaConselho, vinCodigoConselho);
				if (conselhos.size() > 0) {
					ConselhoProfissionalServidorVO conselho = conselhos.get(0);
					assinatura
							.append(conselho.getNome())
							.append(" - ")
							.append(conselho.getSiglaConselho())
							.append((conselho.getNumeroRegistroConselho() != null ? " : "
									+ conselho.getNumeroRegistroConselho()
									: ""));
				}
			}
	
			result.append(assinatura);
			return result.toString();
		}
		return "";
		
	}

	/**
	 * Retorna VO com os dados do cabeçalho.
	 * 
	 * @param item
	 * @return
	 * @throws AGHUNegocioException
	 */
	private AelpCabecalhoLaudoVO criarCabecalho(IAelItemSolicitacaoExames item)
			throws ApplicationBusinessException {
		AelpCabecalhoLaudoVO cabecalhoLaudo = obterCabecalhoLaudo(item);

		verificaSeMostraSeloDeAcreditacao(cabecalhoLaudo, item);
		buscarMaiorDataLiberacao(cabecalhoLaudo, item);

		String medicoSolic = this.buscaNomeMedicoSolicitante(item);

		if (!StringUtils.isBlank(medicoSolic)) {
			cabecalhoLaudo.setMedicoSolicitante("Dr.(a) " + medicoSolic);
		}

		// TODO SIMPLIFICAR
		processarDescricaoConvenioPaciente(cabecalhoLaudo, item);

		cabecalhoLaudo.setLeito(recuperaLeito(item));

		return cabecalhoLaudo;
	}

	private void buscarMaiorDataLiberacao(AelpCabecalhoLaudoVO cabecalhoLaudo,
			IAelItemSolicitacaoExames item) {

		Date dt = null;
		if (item instanceof AelItemSolicitacaoExames) {
			dt = this.getExamesLaudosFacade().buscaMaiorDataLiberacao(
					item.getId().getSoeSeq(), item.getId().getSeqp());
		} else {
			dt = this.getExamesLaudosFacade().buscaMaiorDataLiberacaoHist(
					item.getId().getSoeSeq(), item.getId().getSeqp());
		}

		cabecalhoLaudo.setDataLiberacao(dt);

	}

	private void verificaSeMostraSeloDeAcreditacao(
			AelpCabecalhoLaudoVO cabecalhoLaudo, IAelItemSolicitacaoExames item) {
		AghUnidadesFuncionais unidadeFuncional = item.getSolicitacaoExame()
				.getUnidadeFuncional();

		cabecalhoLaudo
				.setIndMostrarSeloAcreditacao(unidadeFuncional
						.possuiCaracteristica(ConstanteAghCaractUnidFuncionais.USA_SELO_LAUDO));
	}

	private AelpCabecalhoLaudoVO obterCabecalhoLaudo(
			IAelItemSolicitacaoExames item) throws ApplicationBusinessException {
		Short unfSeq = item.getAelUnfExecutaExames().getUnidadeFuncional()
				.getSeq();

		return getExamesPatologiaFacade().obterAelpCabecalhoLaudo(unfSeq);
	}

	private void processarDescricaoConvenioPaciente(
			AelpCabecalhoLaudoVO cabecalhoLaudo, IAelItemSolicitacaoExames item) {
		FatConvenioSaude convenioSaude = item.getSolicitacaoExame()
				.getConvenioSaude();

		if (convenioSaude != null) {
			cabecalhoLaudo.setConvenioPaciente(convenioSaude.getDescricao());
		}

	}

	private String buscaNomeMedicoSolicitante(IAelItemSolicitacaoExames item) {
		AghAtendimentos atendimento = item.getSolicitacaoExame()
				.getAtendimento();
		RapServidores servidorResponsabilidade = item.getSolicitacaoExame()
				.getServidorResponsabilidade();

		if (atendimento != null
				&& atendimento.getAtendimentoPacienteExterno() != null
				&& atendimento.getAtendimentoPacienteExterno()
						.getMedicoExterno() != null) {
			return atendimento.getAtendimentoPacienteExterno()
					.getMedicoExterno().getNome();
		} else if (servidorResponsabilidade != null
				&& servidorResponsabilidade.getPessoaFisica() != null) {
			return servidorResponsabilidade.getPessoaFisica().getNome();
		}

		return "";
	}

	/**
	 * Popula as propriedades de material de analise e regiao anatomica no
	 * exameVO fornecido.
	 * 
	 * @param item
	 * @param exameVO
	 */
	private void populaMaterialAnaliseRegiaoAnatomica(
			final IAelItemSolicitacaoExames item, ExameVO exameVO) {
		AelExames exame = item.getExame();
		AelMateriaisAnalises materialAnalise = item.getMaterialAnalise();
		String descMaterialAnalise = item.getDescMaterialAnalise();
		AelRegiaoAnatomica regiaoAnatomica = item.getRegiaoAnatomica();
		String descRegiaoAnatomica = item.getDescRegiaoAnatomica();

		this.montaNomeExameCabecalho(exame, materialAnalise,
				descMaterialAnalise, regiaoAnatomica, descRegiaoAnatomica,
				exameVO);
	}

	private void montaNomeExameCabecalho(AelExames exame,
			AelMateriaisAnalises materialAnalise, String descMaterialAnalise,
			AelRegiaoAnatomica regiaoAnatomica, String descRegiaoAnatomica,
			ExameVO exameVO) {

		if (materialAnalise.getIndColetavel()) {
			if (!materialAnalise.getIndExigeDescMatAnls()) {
				if (materialAnalise.getDescricao() != null
						&& !materialAnalise.getDescricao().isEmpty()) {
					exameVO.setMaterialAnalise(materialAnalise.getDescricao());
				}
			} else {
				if (descMaterialAnalise != null
						&& !descMaterialAnalise.isEmpty()) {
					exameVO.setMaterialAnalise(descMaterialAnalise);
				}
			}
		}

		if (regiaoAnatomica != null) {
			exameVO.setRegiaoAnatomica(regiaoAnatomica.getDescricao());
		}

		if (descRegiaoAnatomica != null) {
			exameVO.setRegiaoAnatomica(descRegiaoAnatomica);
		}

	}

	private String recuperaLeito(final IAelItemSolicitacaoExames item) {
		String leito = null;
		AghUnidadesFuncionais unidadeFuncional = item.getSolicitacaoExame()
				.getUnidadeFuncional();
		AghAtendimentos atendimento = item.getSolicitacaoExame()
				.getAtendimento();

		boolean isUnidadeEmergencia = unidadeFuncional
				.possuiCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA);

		if (isUnidadeEmergencia) {
			leito = unidadeFuncional.getDescricao();
		} else {
			if (atendimento != null && atendimento.getLeito() != null) {
				leito = "Leito:" + atendimento.getLeito().getLeitoID();
			}
		}

		return leito;
	}

//	public ExamesListaVO buscarDadosLaudo(
//			List<IAelItemSolicitacaoExamesId> itemIds, Map<String, NumeroApTipoVO> solicNumeroAp)
//			throws  ApplicationBusinessException, BaseException {
//		ExamesListaVO result = new ExamesListaVO();
//
//		List<ExameVO> exames = new ArrayList<ExameVO>();
//		IAelItemSolicitacaoExames item = null;
//		
//		for (IAelItemSolicitacaoExamesId id : itemIds) {
//			item = this.getItemSolicitacaoExameIDAO().obterPorChavePrimaria(id);
//
//			ExameVO exame = this.processaItem(item);
//			
//			AelAnatomoPatologico anatomoPatologico = examesPatologiaFacade.obterAelAnatomoPatologicoPorItemSolic(id.getSoeSeq(), id.getSeqp());
//			
//			if (anatomoPatologico != null) {
//				NumeroApTipoVO numeroApTipoVO = solicNumeroAp.get(id.getSoeSeq() + "_" + anatomoPatologico.getConfigExame().getSeq());
//			
//				exame.setNumeroApTipoVO(numeroApTipoVO);
//			}
//			
//			if (exame.getNumeroApTipoVO() == null) {
//				exame.setNomeExame(exame.formataDescricaoExame());
//			}
//			else {
//				exame.setNomeExame(examesLaudosFacade.obterTipoExamePatologico(exame.getNumeroApTipoVO().getNumeroAp(), exame.getNumeroApTipoVO().getLu2Seq()));
//				exame.setListaRecebLiberacao(examesLaudosFacade.montaRecebimentoPatologia(exame.getNumeroApTipoVO()));
//			}
//			
//			exames.add(exame);
//		}
//		Collections.sort(exames);
//
//		Integer prontuario = null;
//		if (item.getSolicitacaoExame().getAtendimento() != null) {
//			prontuario = item.getSolicitacaoExame().getAtendimento().getPaciente().getProntuario();
//		}
//		
//		//------------------
//		if(item.getSolicitacaoExame().getRecemNascido()){
//			result.setNomePaciente("RN de " + getExamesFacade().buscarLaudoNomeMaeRecemNascido(item.getSolicitacaoExame()));
//		}else{
//			result.setNomePaciente(getExamesFacade().buscarLaudoNomePaciente(item.getSolicitacaoExame()) );
//		}
//		prontuario = Integer.valueOf(getExamesFacade().buscarLaudoProntuarioPaciente(item.getSolicitacaoExame()).replaceAll("\\/", ""));
//		
//		result.setProntuario(prontuario);
//		result.setExames(exames);
//
//		this.populaEnderecoContatos(result);
//
//		return result;
//	}
	
	public ExamesListaVO buscarDadosLaudo(List<IAelItemSolicitacaoExamesId> itemIds)
			throws  ApplicationBusinessException, BaseException {
		
		if (itemIds == null || itemIds.isEmpty()){
			throw new IllegalArgumentException("O argumento itemIds é obrigatório");
		}
		ExamesListaVO result = new ExamesListaVO();

		List<ExameVO> exames = new ArrayList<ExameVO>();
		IAelItemSolicitacaoExames item = null;
		
		for (IAelItemSolicitacaoExamesId id : itemIds) {
			item = this.getItemSolicitacaoExameIDAO().obterPorChavePrimaria(id);

			ExameVO exame = this.processaItem(item);
			
			AelAnatomoPatologico anatomoPatologico = examesPatologiaFacade.obterAelAnatomoPatologicoPorItemSolic(id.getSoeSeq(), id.getSeqp());
			
			if (anatomoPatologico == null) {
				exame.setNomeExame(exame.formataDescricaoExame());
			}
			else {
				exame.setNomeExame(anatomoPatologico.getConfigExame().getNome());
				exame.setListaRecebLiberacao(examesLaudosFacade.montaRecebimentoPatologia(anatomoPatologico.getNumeroAp(), anatomoPatologico.getConfigExame().getSeq()));
			}
			
			exames.add(exame);
		}

		Integer prontuario = null;
		if (item != null && item.getSolicitacaoExame() != null 
				&& item.getSolicitacaoExame().getAtendimento() != null
				&& item.getSolicitacaoExame().getAtendimento().getPaciente() != null) {
			prontuario = item.getSolicitacaoExame().getAtendimento().getPaciente().getProntuario();
		}
		
		//------------------
		if(item.getSolicitacaoExame().getRecemNascido()){
			result.setNomePaciente("RN de " + getExamesFacade().buscarLaudoNomeMaeRecemNascido(item.getSolicitacaoExame()));
		}else{
			result.setNomePaciente(getExamesFacade().buscarLaudoNomePaciente(item.getSolicitacaoExame()) );
		}
		prontuario = Integer.valueOf(getExamesFacade().buscarLaudoProntuarioPaciente(item.getSolicitacaoExame()).replaceAll("\\/", ""));
		
		result.setProntuario(prontuario);
		result.setExames(exames);

		this.populaEnderecoContatos(result);

		return result;
	}

	public List<String> montaRecebimentoPatologia(Long numeroAp, Integer lu2Seq) throws ApplicationBusinessException {
		
		List<String> listaRecebLiberacao = new ArrayList<String>();
		
		String situacao = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		
		List<DataRecebimentoSolicitacaoVO> listaDataRecebimentoSolicitacaoVO = getAelItemSolicitacaoExameDAO().listarDataRecebimentoTipoExamePatologico(numeroAp, lu2Seq, situacao);
		
		for (DataRecebimentoSolicitacaoVO vo : listaDataRecebimentoSolicitacaoVO) {
			
			listaRecebLiberacao.add("Recebimento material: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR")).format(vo.getDataRecebimento()) + " (Solicitação: " + vo.getSoeSeq() + ")");
		}

		List<DataLiberacaoVO> listaDataLiberacao = getAelItemSolicitacaoExameDAO().listarDataLiberacaoTipoExamePatologico(numeroAp, lu2Seq);
		
		for (DataLiberacaoVO dataLiberacao : listaDataLiberacao) {
			
			if(dataLiberacao.getDataLiberacao() !=null){
				listaRecebLiberacao.add("Liberação: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR")).format(dataLiberacao.getDataLiberacao()));
			}
		}
		
		return listaRecebLiberacao;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	
	/** GET **/

	protected InformacoesColetaON getInformacoesColetaON() {
		return this.informacoesColetaON;
	}

	protected AelItemSolicitacaoExameIDAO getItemSolicitacaoExameIDAO() {
		return this.aelItemSolicitacaoExameIDAO;
	}

	protected AelResultadoExameIDAO getAelResultadoExameIDAO() {
		return this.aelResultadoExameIDAO;
	}

	protected AelExtratoItemSolicitacaoIDAO getAelExtratoItemSolicitacaoIDAO() {
		return this.aelExtratoItemSolicitacaoIDAO;
	}

	protected MascaraExamesRN getMascaraExamesRN() {
		return this.mascaraExamesRN;
	}

	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}

	protected IExamesPatologiaFacade getExamesPatologiaFacade() {
		return this.examesPatologiaFacade;
	}

	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected AelParametroCamposLaudoDAO getAelParametroCamposLaudoDAO() {
		return this.aelParametroCamposLaudoDAO;
	}

	protected AelCampoVinculadoDAO getAelCampoVinculadoDAO() {
		return this.aelCampoVinculadoDAO;
	}

	protected AelResultadoPadraoCampoDAO getAelResultadoPadraoCampoDAO() {
		return this.aelResultadoPadraoCampoDAO;
	}

	protected AelDescricoesResulPadraoDAO getAelDescricoesResulPadraoDAO() {
		return this.aelDescricoesResulPadraoDAO;
	}

}