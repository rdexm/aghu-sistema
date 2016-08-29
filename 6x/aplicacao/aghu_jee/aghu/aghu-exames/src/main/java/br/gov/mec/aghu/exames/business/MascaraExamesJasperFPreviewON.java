package br.gov.mec.aghu.exames.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioCumpriuJejumColeta;
import br.gov.mec.aghu.dominio.DominioExibicaoParametroCamposLaudo;
import br.gov.mec.aghu.dominio.DominioFormaRespiracao;
import br.gov.mec.aghu.dominio.DominioLocalColetaAmostra;
import br.gov.mec.aghu.dominio.DominioObjetoVisual;
import br.gov.mec.aghu.exames.dao.AelCampoVinculadoDAO;
import br.gov.mec.aghu.exames.dao.AelDescricoesResulPadraoDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoIDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameIDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameIDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoPadraoCampoDAO;
import br.gov.mec.aghu.exames.dao.VAelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.laudos.EnderecoContatosVO;
import br.gov.mec.aghu.exames.laudos.ExameVO;
import br.gov.mec.aghu.exames.laudos.ExamesListaVO;
import br.gov.mec.aghu.exames.laudos.LaudoMascara;
import br.gov.mec.aghu.exames.laudos.MascaraVO;
import br.gov.mec.aghu.exames.laudos.NotaAdicionalVO;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.vo.AelpCabecalhoLaudoVO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelCampoVinculado;
import br.gov.mec.aghu.model.AelDescricoesResulPadrao;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelInformacaoColeta;
import br.gov.mec.aghu.model.AelInformacaoMdtoColeta;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.IAelResultadoExame;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength" })
@Stateless
public class MascaraExamesJasperFPreviewON extends BaseBusiness {

	private static final long serialVersionUID = 2831485648403262351L;
	
	private static final Log LOG = LogFactory.getLog(MascaraExamesJasperFPreviewON.class);

	public enum MascaraExamesJasperFPreviewONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_UNF_INEXISTENTE;
	}

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
	private IAghuFacade aghuFacade;
	
	@EJB
	private IMascaraExamesFacade mascaraExamesFacade;
	
	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	@Inject
	private AelCampoVinculadoDAO aelCampoVinculadoDAO;

	@Inject
	private AelResultadoPadraoCampoDAO aelResultadoPadraoCampoDAO;

	@Inject
	private AelDescricoesResulPadraoDAO aelDescricoesResulPadraoDAO;

	@Inject
	private AelItemSolicitacaoExameIDAO aelItemSolicitacaoExameIDAO;

	@Inject
	private AelResultadoExameIDAO aelResultadoExameIDAO;

	@Inject
	private AelExtratoItemSolicitacaoIDAO aelExtratoItemSolicitacaoIDAO;

	@Inject
	private InformacoesColetaON informacoesColetaON;

	@Inject
	private VAelUnfExecutaExamesDAO vAelUnfExecutaExamesDAO;
	
	@Inject
	private MascaraExamesRN mascaraExamesRN;

	private List<AelParametroCamposLaudo> obterListaCamposExibicao(			
			Map<AelParametroCamposLaudo, AelResultadoExame> resultados,
			List<AelParametroCamposLaudo> camposDaVersao) {
		List<AelParametroCamposLaudo> result = new ArrayList<AelParametroCamposLaudo>();
		if (camposDaVersao == null) {
			return result;
		}

		for (AelParametroCamposLaudo aelParametroCampoLaudo : camposDaVersao) {

			/* Pega todos os campos que vincularam o campo */
			List<AelCampoVinculado> camposVinculadores = getAelCampoVinculadoDAO()
					.pesquisarCampoVinculadoresPorParametroCampoLaudo(
							aelParametroCampoLaudo);
			
			boolean hasResults = true;

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

	private boolean processarResultadoEDescricaoExame(
			Map<AelParametroCamposLaudo, AelResultadoExame> resultados,
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
	private ExameVO processaItem(List<AelParametroCamposLaudo> parametrosPrevia, AelVersaoLaudo versaoLaudo)
			throws BaseException {

		ExameVO exameVO = null;
		VAelUnfExecutaExames unfExec = vAelUnfExecutaExamesDAO.obterVAelUnfExecutaExames(versaoLaudo.getId().getEmaExaSigla(), versaoLaudo.getId().getEmaManSeq(), "A");
		
		if (unfExec == null) {
			unfExec = vAelUnfExecutaExamesDAO.obterVAelUnfExecutaExames(versaoLaudo.getId().getEmaExaSigla(), versaoLaudo.getId().getEmaManSeq(), "I");
		}
		
		if (unfExec == null) {
			throw new ApplicationBusinessException(MascaraExamesJasperFPreviewONExceptionCode.MENSAGEM_UNF_INEXISTENTE);
		}
		AghUnidadesFuncionais unf = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfExec.getId().getUnfSeq());
		AelpCabecalhoLaudoVO cabecalho = null;
		
		if (unf != null) {
			cabecalho = this.criarCabecalho(versaoLaudo, unf);
			AelExames exame = examesFacade.obterAelExamesPeloId(versaoLaudo.getId().getEmaExaSigla());
			AelMateriaisAnalises materialAnalise = examesFacade.buscarMaterialAnalisePorSeq(versaoLaudo.getId().getEmaManSeq());
			
			exameVO = new ExameVO();
			exameVO.setSolicitacao(999999);
			exameVO.setItem(Short.valueOf("1"));
			exameVO.setSigla(versaoLaudo.getId().getEmaExaSigla());
			exameVO.setDescricao(exame.getDescricao());
			exameVO.setConvenio(cabecalho.getConvenioPaciente());
			this.populaMaterialAnaliseRegiaoAnatomica(exame, materialAnalise, materialAnalise.getDescricao(),
					null, null, exameVO);

			exameVO.setOrigem(null);

			// dados do serviço da unidade executora
			exameVO.setServico(cabecalho.getpDescServico()); // ok
			exameVO.setRegistroConselhoServico(cabecalho.getpRegistro()); // ok
			exameVO.setChefeServico(cabecalho.getpLinhaCab2()); // ok
			exameVO.setConselhoChefeServico(cabecalho.getpConselho()); // ok
			exameVO.setNroConselhoChefeServico(cabecalho.getpNroConselho()); // ok

			FccCentroCustos centroCustoServico = this.buscarCentroCustoServico(unf);
			if (centroCustoServico != null) {
				String templateFone = "(55)(%1$s) %2$s";
				EnderecoContatosVO enderecoContatos = new EnderecoContatosVO();
				String cnpj = this.getParametroFacade().buscarValorTexto(
						AghuParametrosEnum.P_HOSPITAL_CGC);
				enderecoContatos.setCnpj(cnpj);
				enderecoContatos
						.setFax(String.format(templateFone,
								centroCustoServico.getDddFax(),
								centroCustoServico.getFax()));
				enderecoContatos.setFone(String.format(templateFone,
						centroCustoServico.getDddFone(),
						centroCustoServico.getFone()));
				enderecoContatos.setEmail(centroCustoServico.getEmail());
				enderecoContatos
						.setCaixaPostal(centroCustoServico.getCaixaPostal());
				enderecoContatos.setHomepage(centroCustoServico.getHomePage());
				enderecoContatos.setEndereco(cabecalho.getpLinhaCab4());
				exameVO.setEnderecoContatos(enderecoContatos);
			}

			// dados da unidade executora
			exameVO.setUnidade(cabecalho.getpDescUnidade()); // ok
			exameVO.setChefeUnidade(cabecalho.getpChefeUnidade()); // ok
			exameVO.setConselhoUnidade(cabecalho.getpConselhoUnid()); // ok
			exameVO.setNroConselhoUnidade(cabecalho.getpNroConselhoUnid()); // ok
			exameVO.setAssinaturaMedico("Dr.(a) Testes  - PVA:123456789");
			
			String assinaturaEletronica = this.criarAssinaturaEletronica(unf, exameVO);
			exameVO.setAssinaturaEletronica(assinaturaEletronica);

			String nomeMedicoSolicitante = this.buscaNomeMedicoSolicitante();
			exameVO.setNomeMedicoSolicitante(nomeMedicoSolicitante);

			// respiracao
			exameVO.setInformacoesRespiracao(criarInformacaoRespiracao(DominioFormaRespiracao.TRES, null, Short.valueOf("50")));

			// recebimento
			exameVO.setRecebimento(new Date());

			// informacoes coleta
			exameVO.setInformacoesColeta(this.preencherInformacoesColeta());

			// notas adicionais
			this.populaNotasAdicionais(exameVO);
			
			this.populaResultadosCamposLaudo(parametrosPrevia, exameVO, versaoLaudo);
		}
		
		return exameVO;
	}

	private NotaAdicionalVO preencherNotaAdicionalVO(String nota, String servidor) {
		NotaAdicionalVO vo = new NotaAdicionalVO();
		vo.setNota(nota);
		vo.setCriadoEm(new Date());
		vo.setCriadoPor(servidor);
		
		return vo;
	}
	
	private void populaNotasAdicionais(ExameVO exame) {
		List<NotaAdicionalVO> notasvo = new ArrayList<NotaAdicionalVO>();
		notasvo.add(preencherNotaAdicionalVO("Teste Prévia Nota Adicional 01", "Nome Pessoa Servidor 01"));
		notasvo.add(preencherNotaAdicionalVO("Teste Prévia Nota Adicional 11", "Nome Pessoa Servidor 11"));
		notasvo.add(preencherNotaAdicionalVO("Teste Prévia Nota Adicional 21", "Nome Pessoa Servidor 21"));		
		
		exame.setNotas(notasvo);
	}

	private List<String> preencherInformacoesColeta() {
	
		List<String> listaResult = new ArrayList<String>();
		
		AelInformacaoColeta infoColeta = new AelInformacaoColeta();
		infoColeta.setCumpriuJejum(DominioCumpriuJejumColeta.S);
		infoColeta.setJejumRealizado("12hs");
		infoColeta.setDocumento(true);
		infoColeta.setLocalColeta(DominioLocalColetaAmostra.C);
		infoColeta.setInfMenstruacao(false);
		infoColeta.setDtUltMenstruacao(new Date());

		/* Informações de coleta */
		AelInformacaoMdtoColeta infoCol = new AelInformacaoMdtoColeta();
		infoColeta.setInfMedicacao(true);
		infoCol.setMedicamento("sabutamol");
		infoCol.setDthrIngeriu(new Date());
		infoCol.setDthrColetou(new Date());

		infoColeta.getInformacaoMdtoColetaes().add(infoCol);

		infoColeta.setInformacoesAdicionais("Teste prévia informações adicionais");

		getInformacoesColetaON().processarInformacoesColeta(listaResult, infoColeta);
		
		return listaResult;
	}
	
	/**
	 * Trata os resultados e campos do laudo populando os vo de exames.
	 * 
	 * @param item
	 * @param exame
	 * @throws MECBaseException
	 */
	private void populaResultadosCamposLaudo(List<AelParametroCamposLaudo> camposDaVersaoLaudoPrevia, ExameVO exame, AelVersaoLaudo versaoLaudo) throws BaseException {
		// busca os valores de resultado para cada campo do laudo
		Map<AelParametroCamposLaudo, AelResultadoExame> resultados = mascaraExamesFacade
				.obterMapaResultadosFicticiosExames(camposDaVersaoLaudoPrevia);

		// busca todos os campos do laudo
		List<AelParametroCamposLaudo> camposDaVersaoLaudo = this
				.getAelParametroCamposLaudoDAO()
				.pesquisarCamposRelatorioPorVersaoLaudo(versaoLaudo);

		// adiciona os campos q não tem resultado
		for (AelParametroCamposLaudo campo : camposDaVersaoLaudo) {
			if (!resultados.containsKey(campo)) {
				resultados.put(campo, null);
			}
		}

		// buscar resultado
		Map<AelParametroCamposLaudo, String> c = new HashMap<AelParametroCamposLaudo, String>();
		for (Map.Entry<AelParametroCamposLaudo, AelResultadoExame> entry : resultados
				.entrySet()) {
			AelParametroCamposLaudo campo = entry.getKey();
			getAelParametroCamposLaudoDAO().desatachar(campo);
			if (DominioObjetoVisual.TEXTO_LONGO.equals(campo.getObjetoVisual())) {
				campo.setLarguraObjetoVisual((short) LaudoMascara.TAMANHO_MAX);
				//campo.setAlturaObjetoVisual(LaudoMascara.ALTURA_MIN_TEXTO_LONGO);
			}
			String s = this.extraiResultadoString(entry);
			c.put(entry.getKey(), s);
		}

		Map<AelParametroCamposLaudo, String> listaFinal = new HashMap<AelParametroCamposLaudo, String>();

		// adicionar a listaFinal apenas os campos que devem ser exibidos de
		// acordo com a vinculação cadastrada
		List<AelParametroCamposLaudo> exibir = this.obterListaCamposExibicao(resultados, camposDaVersaoLaudo);
		for (Map.Entry<AelParametroCamposLaudo, String> entry : c.entrySet()) {
			AelParametroCamposLaudo key = entry.getKey();
			if (exibir.contains(key)) {
				listaFinal.put(key, entry.getValue());
			}
		}

		exame.setMostraDescricao(versaoLaudo.getImprimeNomeExame());
		
		populaCamposMascara(listaFinal, exame);
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

			mascara.setAlinhamento(key.getAlinhamento().name().toString()
					.toCharArray()[0]);
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

	@SuppressWarnings({"PMD.MissingBreakInSwitch", "PMD.UnusedPrivateMethod"})
	private String extraiResultadoString(
			Entry<AelParametroCamposLaudo, AelResultadoExame> entry)
			throws BaseException {

		AelParametroCamposLaudo parametro = entry.getKey();
		IAelResultadoExame resultado = (IAelResultadoExame) entry.getValue();

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
			String textoNumericoExpressao = null;
			if (resultado != null) {
				Short decimais = parametro.getQuantidadeCasasDecimais() == null ? (short) 0
						: parametro.getQuantidadeCasasDecimais();

				String valor = resultado.getValor() == null ? null : resultado
						.getValor().toString();
				if (valor != null && decimais.intValue() != 0) {
					// exemplo: valor 35 decimais 2 = 0,35
					String aux = StringUtils.leftPad(valor, decimais + 1, "0");
					StringBuffer v = new StringBuffer(aux);
					v.insert(v.length() - decimais, ",");
					textoNumericoExpressao = v.toString();
				} else {
					textoNumericoExpressao = valor;
				}
			}

			return textoNumericoExpressao;

		case TEXTO_LONGO:
			if (resultado == null || resultado.getDescricao() == null) {
				return null;
			}
			return resultado.getDescricao();

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
			
			return "Equipamento Teste Prévia";

		case METODO:
			return "Método Teste Prévia";

		case RECEBIMENTO:
			return "Recebimento Teste Prévia";

		case HISTORICO:
			return "Histórico Teste Prévia";
			
		case VALORES_REFERENCIA:
			String vlrReferencia = parametro.getTextoLivre();
			if (vlrReferencia != null) {
				vlrReferencia = vlrReferencia.trim();
			} else {
				vlrReferencia = "";
			}
			return vlrReferencia;
		}

		return null;
	}

	private String criarInformacaoRespiracao(DominioFormaRespiracao formaRespiracao, BigDecimal litrosOxigenio, Short percOxigenio)
			throws BaseException {
		String result = null;

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

	private String criarAssinaturaEletronica(AghUnidadesFuncionais unf,
			ExameVO exame) throws BaseException {
		boolean uniFechada = unf.possuiCaracteristica(ConstanteAghCaractUnidFuncionais.AREA_FECHADA);
		if (!uniFechada){
			StringBuffer result = new StringBuffer(50);
			StringBuffer assinatura = new StringBuffer(50);
	
			Integer matriculaConselho = null;
			Short vinCodigoConselho = null;
	
			boolean chefiaAssEletro = unf.possuiCaracteristica(
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
	private AelpCabecalhoLaudoVO criarCabecalho(AelVersaoLaudo versaoLaudo, AghUnidadesFuncionais unf)
			throws ApplicationBusinessException {
		
		AelpCabecalhoLaudoVO cabecalhoLaudo = obterCabecalhoLaudo(unf);
	
		verificaSeMostraSeloDeAcreditacao(cabecalhoLaudo, unf);
		buscarMaiorDataLiberacao(cabecalhoLaudo);

		String medicoSolic = this.buscaNomeMedicoSolicitante();

		if (!StringUtils.isBlank(medicoSolic)) {
			cabecalhoLaudo.setMedicoSolicitante("Dr.(a) " + medicoSolic);
		}

		cabecalhoLaudo.setLeito(recuperaLeito(unf));
		
		return cabecalhoLaudo;
	}

	private void buscarMaiorDataLiberacao(AelpCabecalhoLaudoVO cabecalhoLaudo) {
		cabecalhoLaudo.setDataLiberacao(new Date());
	}

	private void verificaSeMostraSeloDeAcreditacao(
			AelpCabecalhoLaudoVO cabecalhoLaudo, AghUnidadesFuncionais unf) {
		cabecalhoLaudo
				.setIndMostrarSeloAcreditacao(unf
						.possuiCaracteristica(ConstanteAghCaractUnidFuncionais.USA_SELO_LAUDO));
	}

	private AelpCabecalhoLaudoVO obterCabecalhoLaudo(AghUnidadesFuncionais unf) throws ApplicationBusinessException {
		return getExamesPatologiaFacade().obterAelpCabecalhoLaudo(unf.getSeq());
	}

	private String buscaNomeMedicoSolicitante() {
		return "Médico Solicitante do Exame Teste";
	}

	/**
	 * Popula as propriedades de material de analise e regiao anatomica no
	 * exameVO fornecido.
	 * 
	 * @param item
	 * @param exameVO
	 */
	private void populaMaterialAnaliseRegiaoAnatomica(
			AelExames exame, AelMateriaisAnalises materialAnalise,  String descMaterialAnalise,
			AelRegiaoAnatomica regiaoAnatomica, String descRegiaoAnatomica, ExameVO exameVO) {
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

	private String recuperaLeito(AghUnidadesFuncionais unidadeFuncional) {
		String leito = null;

		boolean isUnidadeEmergencia = unidadeFuncional
				.possuiCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA);

		if (isUnidadeEmergencia) {
			leito = unidadeFuncional.getDescricao();
		} else {
			leito = "Leito: Teste01";
		}

		return leito;
	}

	public ExamesListaVO buscarDadosLaudo(List<AelParametroCamposLaudo> parametrosPrevia, AelVersaoLaudo versaoLaudo) throws  BaseException {
		ExamesListaVO result = new ExamesListaVO();

		result.setProntuario(123456789);
		result.setNomePaciente("Paciente Teste do Teste");
		List<ExameVO> exames = new ArrayList<ExameVO>();
		ExameVO exame = this.processaItem(parametrosPrevia, versaoLaudo);
		exames.add(exame);
		result.setExames(exames);

		this.populaEnderecoContatos(result);

		return result;
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