package br.gov.mec.aghu.exames.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.DadosExameMaterialAnaliseVO;
import br.gov.mec.aghu.exames.vo.ExameMaterialAnaliseVO;
import br.gov.mec.aghu.exames.vo.ExameSignificativoVO;
import br.gov.mec.aghu.exames.vo.InformacaoExame;
import br.gov.mec.aghu.exames.vo.ItemSolicitacaoExame;
import br.gov.mec.aghu.exames.vo.RegiaoAnatomicaVO;
import br.gov.mec.aghu.exames.vo.SolicitacaoExamesVO;
import br.gov.mec.aghu.exames.vo.SolicitacaoExamesValidaVO;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnidExameSignificativo;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.service.ServiceException;

/**
 * 
 * @author felipe
 *
 */


@Stateless
public class ExamesServiceImpl implements IExamesService{

	private static final String MSG_PARAMETROS_OBRIGATORIOS = "Parâmetros obrigatórios";


	@EJB
	private IExamesFacade examesFacade;
	
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@Inject
    private MessagesUtils messagesUtils;

	/**
	 * #34384 - Obter solicitação de exames	
	 * @param atdSeq
	 * @return
	 */
	@Override
	public List<SolicitacaoExamesVO> obterSolicitacaoExamesPorAtendimento (Integer atdSeq) throws ServiceException{
		List<AelSolicitacaoExames> exames =  this.examesFacade.obterSolicitacaoExamesPorAtendimento(atdSeq);
		// transforma para o tipo de retorno
		List<SolicitacaoExamesVO> listaRetorno = new ArrayList<SolicitacaoExamesVO>();
		for (AelSolicitacaoExames exame : exames) {
			SolicitacaoExamesVO vo = new SolicitacaoExamesVO();
			vo.setSeq(exame.getSeq());
			vo.setCriadoEm(exame.getCriadoEm());
			if(exame.getServidorResponsabilidade() != null){
				vo.setSerMatricula(exame.getServidorResponsabilidade().getId().getMatricula());
				vo.setSerVinculo(exame.getServidorResponsabilidade().getId().getVinCodigo());
			}
			listaRetorno.add(vo);
		}
		return listaRetorno;	
	}
	
	@Override
	public List<ItemSolicitacaoExame> listarItemSolicitacaoExamePorSiglaMaterialPaciente(String siglaExame, Integer seqMaterial, Integer codPaciente)
			throws ServiceException {

		if (StringUtils.isBlank(siglaExame) || seqMaterial == null || codPaciente == null) {
			throw new ServiceException(MSG_PARAMETROS_OBRIGATORIOS);
		}
		try {
			List<ItemSolicitacaoExame> result = new ArrayList<ItemSolicitacaoExame>();
			List<AelItemSolicitacaoExamesId> itens = examesFacade.listarAelItemSolicitacaoExamesPorSiglaMaterialPaciente(siglaExame, seqMaterial, codPaciente);
			if (itens != null && !itens.isEmpty()) {
				for (AelItemSolicitacaoExamesId itemId : itens) {
					result.add(new ItemSolicitacaoExame(itemId.getSoeSeq(), itemId.getSeqp()));
				}
			}
			return result;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public InformacaoExame buscarInformacaoExamePorItem(Integer soeSeq, Short seqp) throws ServiceException {
		if (soeSeq == null || seqp == null) {
			throw new ServiceException(MSG_PARAMETROS_OBRIGATORIOS);
		}
		try {
			// Executar a consulta C29 passando o número da solicitação de exames e número do item de solicitação de exames.
			List<AelResultadoExame> resultados = examesFacade.pesquisarAelResultadoExamePorAelItemSolicitacaoExames(soeSeq, seqp);

			// Não encontrou resultado retorna NULO.
			// Encontrou resultado e tem mais de um registro retornar NULO.
			if (resultados != null && resultados.size() == 1) {

				// resultadoExame = C29
				AelResultadoExame resultadoExame = resultados.get(0);

				// Encontrou resultado e o C29.valor encontrado é diferente de NULO retorna o C29.valor.
				if (resultadoExame.getValor() != null) {
					Short casasDecimais = null;
					if (resultadoExame.getParametroCampoLaudo() != null) {
						casasDecimais = resultadoExame.getParametroCampoLaudo().getQuantidadeCasasDecimais();
					}
					return this.obterValor(resultadoExame.getValor(), casasDecimais);
				}
				//
				// Encontrou resultado e o C29.valor é NULO continuar os passos abaixo.
				//
				// Se o campo C29.rcd_gtc_seq é diferente de NULO E C29. rcd_seqp é diferente de NULO executar a consulta C30 passando os valores
				// C29.rcd_gtc_seq
				// (Código do grupo de codificados) e C29.rcd_seqp (Sequencial dos códigos codificados) como parâmetros e executar os passos abaixo.
				if (resultadoExame.getResultadoCodificado() != null) {
					String descricao = examesFacade.obterAelResultadoCodificadoDescricao(resultadoExame.getResultadoCodificado().getId().getGtcSeq(),
							resultadoExame.getResultadoCodificado().getId().getSeqp());
					return this.obterDescricao(descricao);
				}
				//
				// Se C29.cac_seq é diferente de NULO executar a consulta C31 passando os valores C29.cac_seq (Código da característica cardiológica) como
				// parâmetro.
				if (resultadoExame.getResultadoCaracteristica() != null) {
					String descricao = examesFacade.obterAelResultadoCaracteristicaDescricao(resultadoExame.getResultadoCaracteristica().getSeq());
					return this.obterDescricao(descricao);
				}
				//
				// Executar a consulta C32 passando os valores:
				// - soe_seq – número da solicitação de exames recebido como parâmetro
				// - seqp – número do item de solicitação de exames recebido como parâmetro
				// - C29.pcl_vel_ema_exa_sigla - Sigla ou código do exame
				// - C29.pcl_vel_ema_man_seq - Código do material de análise
				// - C29.pcl_vel_seqp - Número da versão do laudo
				// - C29.pcl_cal_seq - Sequencia do campo do laudo
				// - C29.pcl_seqp - Número sequencial do campo
				// - C29.seqp - Sequencial dos resultados de exames
				String descricao = resultadoExame.getDescricao();
				return this.obterDescricao(descricao);
			}
			return null;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	private InformacaoExame obterDescricao(String descricao) {
		if (descricao != null && descricao.length() <= 20) {
			// 2. Encontrou resultado e a descrição é menor ou igual a 20 retornar os primeiros 20 caracteres.
			InformacaoExame informacaoExame = new InformacaoExame();
			informacaoExame.setDescricao(descricao);
			return informacaoExame;
		}
		// 1. Não encontrou resultado retorna NULO.
		// 3. Encontrou resultado e a descrição é maior que 20 retornar NULO.
		return null;
	}

	private InformacaoExame obterValor(Long valor, Short casasDecimais) {
		if (valor != null && casasDecimais != null) {
			InformacaoExame informacaoExame = new InformacaoExame();
			// ree.valor / power(10,pcl.qtde_casas_decimais) valor
			// O power(n, m), função matemática relacionada a potência, onde n é a base e o m é o expoente, por exemplo power(5, 3) retorna 125.
			BigDecimal val = BigDecimal.valueOf(valor);
			BigDecimal pot = BigDecimal.valueOf(Math.pow(10d, casasDecimais.doubleValue()));
			informacaoExame.setValor(val.divide(pot));
			return informacaoExame;
		}
		return null;
	}

	@Override
	public List<ExameSignificativoVO> pesquisarUnidadesFuncionaisExamesSignificativosPerinato(Short unfSeq, String siglaExame, Integer seqMatAnls,
			Boolean indCargaExame, int firstResult, int maxResults) throws ServiceException {
		try {
			List<ExameSignificativoVO> result = new ArrayList<ExameSignificativoVO>();
			List<AelUnidExameSignificativo> listaExamesSignificativos = examesFacade.pesquisarUnidadesFuncionaisExamesSignificativosPerinato(unfSeq,
					siglaExame, seqMatAnls, indCargaExame, firstResult, maxResults);
			if (listaExamesSignificativos != null && !listaExamesSignificativos.isEmpty()) {
				for (AelUnidExameSignificativo aelUnidExameSignificativo : listaExamesSignificativos) {
					ExameSignificativoVO exameSignificativoVO = new ExameSignificativoVO();

					exameSignificativoVO.setUnfSeq(aelUnidExameSignificativo.getId().getUnfSeq());
					exameSignificativoVO.setEmaManSeq(aelUnidExameSignificativo.getId().getEmaManSeq());
					exameSignificativoVO.setEmaExaSigla(aelUnidExameSignificativo.getId().getEmaExaSigla());
					exameSignificativoVO.setIndCargaExame(aelUnidExameSignificativo.getIndCargaExame());

					if (aelUnidExameSignificativo.getAghUnidadesFuncionais() != null) {
						exameSignificativoVO.setUnidadeFuncional(aelUnidExameSignificativo.getAghUnidadesFuncionais().getDescricao());
					} else {
						exameSignificativoVO.setUnidadeFuncional(null);
					}

					if (aelUnidExameSignificativo.getAelExamesMaterialAnalise() != null) {
						exameSignificativoVO.setSiglaExame(aelUnidExameSignificativo.getAelExamesMaterialAnalise().getId().getExaSigla());

						if (aelUnidExameSignificativo.getAelExamesMaterialAnalise().getAelExames() != null) {
							exameSignificativoVO.setExame(aelUnidExameSignificativo.getAelExamesMaterialAnalise().getAelExames().getDescricao());
						} else {
							exameSignificativoVO.setExame(null);
						}

						if (aelUnidExameSignificativo.getAelExamesMaterialAnalise().getAelMateriaisAnalises() != null) {
							exameSignificativoVO.setMaterialAnalise(aelUnidExameSignificativo.getAelExamesMaterialAnalise().getAelMateriaisAnalises()
									.getDescricao());
						} else {
							exameSignificativoVO.setMaterialAnalise(null);
						}

					} else {
						exameSignificativoVO.setSiglaExame(null);
						exameSignificativoVO.setExame(null);
						exameSignificativoVO.setMaterialAnalise(null);
					}

					result.add(exameSignificativoVO);
				}
			}
			return result;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Long pesquisarUnidadesFuncionaisExamesSignificativosPerinatoCount(Short unfSeq, String siglaExame, Integer seqMatAnls, Boolean indCargaExame) throws ServiceException {
		try {
			return examesFacade.pesquisarUnidadesFuncionaisExamesSignificativosPerinatoCount(unfSeq, siglaExame, seqMatAnls, indCargaExame);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<ExameMaterialAnaliseVO> pesquisarAtivosPorSiglaOuDescricao(String parametro) throws ServiceException {
		return this.pesquisarAtivosPorSiglaOuDescricao(parametro, 100);
	}

	@Override
	public List<ExameMaterialAnaliseVO> pesquisarAtivosPorSiglaOuDescricao(String parametro, Integer maxResults) throws ServiceException {
		try {
			List<ExameMaterialAnaliseVO> result = new ArrayList<ExameMaterialAnaliseVO>();
			List<AelExamesMaterialAnalise> exames = examesFacade.pesquisarAtivosPorSiglaOuDescricao(parametro, maxResults);
			if (exames != null && !exames.isEmpty()) {
				for (AelExamesMaterialAnalise aelExamesMaterialAnalise : exames) {
					ExameMaterialAnaliseVO exameMaterialAnaliseVO = new ExameMaterialAnaliseVO();
					exameMaterialAnaliseVO.setSigla(aelExamesMaterialAnalise.getId().getExaSigla());
					exameMaterialAnaliseVO.setMamSeq(aelExamesMaterialAnalise.getId().getManSeq());
					if (aelExamesMaterialAnalise.getAelExames() != null) {
						exameMaterialAnaliseVO.setDescricao(aelExamesMaterialAnalise.getAelExames().getDescricao());
					}
					if (aelExamesMaterialAnalise.getAelMateriaisAnalises() != null) {
						exameMaterialAnaliseVO.setMaterial(aelExamesMaterialAnalise.getAelMateriaisAnalises().getDescricao());
					}
					result.add(exameMaterialAnaliseVO);
				}
			}
			return result;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Long pesquisarAtivosPorSiglaOuDescricaoCount(String parametro) throws ServiceException {
		try {
			return examesFacade.pesquisarAtivosPorSiglaOuDescricaoCount(parametro);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public void persistirAelUnidExameSignificativo(Short unfSeq, String exaSigla, Integer matAnlsSeq, Date data, Integer matricula, Short vinCodigo, Boolean indPreNatal, Boolean indCargaExame) throws ServiceException {
		if (unfSeq == null || StringUtils.isBlank(exaSigla) || matAnlsSeq == null) {
			throw new ServiceException(MSG_PARAMETROS_OBRIGATORIOS);
		}
		try {
			examesFacade.persistirAelUnidExameSignificativo(unfSeq, exaSigla, matAnlsSeq, data, matricula, vinCodigo, indPreNatal, indCargaExame);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public void removerAelUnidExameSignificativo(Short unfSeq, String exaSigla, Integer matAnlsSeq) throws ServiceException {
		if (unfSeq == null || StringUtils.isBlank(exaSigla) || matAnlsSeq == null) {
			throw new ServiceException(MSG_PARAMETROS_OBRIGATORIOS);
		}
		try {
			examesFacade.removerAelUnidExameSignificativo(unfSeq, exaSigla, matAnlsSeq);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<ExameSignificativoVO> pesquisarAelUnidExameSignificativoPorUnfSeq(Short unfSeq, Boolean indCargaExame) {
		List<ExameSignificativoVO> result = new ArrayList<ExameSignificativoVO>();
		List<AelUnidExameSignificativo> listaExamesSignificativos = examesFacade.pesquisarAelUnidExameSignificativoPorUnfSeq(unfSeq, indCargaExame);
		if (listaExamesSignificativos != null && !listaExamesSignificativos.isEmpty()) {
			for (AelUnidExameSignificativo aelUnidExameSignificativo : listaExamesSignificativos) {
				ExameSignificativoVO exameSignificativoVO = new ExameSignificativoVO();

				exameSignificativoVO.setUnfSeq(aelUnidExameSignificativo.getId().getUnfSeq());
				exameSignificativoVO.setEmaManSeq(aelUnidExameSignificativo.getId().getEmaManSeq());
				exameSignificativoVO.setEmaExaSigla(aelUnidExameSignificativo.getId().getEmaExaSigla());

				result.add(exameSignificativoVO);
			}
		}
		return result;
	}
	
	@Override
	public List<DadosExameMaterialAnaliseVO> pesquisarExamesPorSiglaMaterialAnalise(String sigla, Integer seqMatAnls) throws ServiceException {
		try {
			List<DadosExameMaterialAnaliseVO> result = new ArrayList<DadosExameMaterialAnaliseVO>();
			List<AelExamesMaterialAnalise> exames = examesFacade.pesquisarExamesPorSiglaMaterialAnalise(sigla, seqMatAnls);
			if (exames != null && !exames.isEmpty()) {
				for (AelExamesMaterialAnalise aelExamesMaterialAnalise : exames) {
					DadosExameMaterialAnaliseVO dadosExameMaterialAnaliseVO = new DadosExameMaterialAnaliseVO();
					dadosExameMaterialAnaliseVO.setSigla(aelExamesMaterialAnalise.getId().getExaSigla());
					StringBuffer descricao = new StringBuffer();
					if (aelExamesMaterialAnalise.getAelExames() != null) {
						descricao.append(aelExamesMaterialAnalise.getAelExames().getDescricaoUsual());
					}
					if (aelExamesMaterialAnalise.getAelMateriaisAnalises() != null) {
						if (descricao.length() > 0) {
							descricao.append(" - ");
						}
						descricao.append(aelExamesMaterialAnalise.getAelMateriaisAnalises().getDescricao());
					}
					if (descricao.length() > 0) {
						dadosExameMaterialAnaliseVO.setDescricao(descricao.toString());
					}
					result.add(dadosExameMaterialAnaliseVO);
				}
			}
			return result;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * #39003 - Serviço que busca ultima solicitacao de exames
	 * @param atdSeq
	 * @return
	 * @throws ServiceException 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public SolicitacaoExamesValidaVO buscarUltimaSolicitacaoExames(Integer atdSeq) throws ServiceException{
		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		SolicitacaoExamesValidaVO vo = new SolicitacaoExamesValidaVO();
		try {
			solicitacaoExame = this.solicitacaoExameFacade.buscarUltimaSolicitacaoExames(atdSeq);
		} catch (ApplicationBusinessException e) {
			throw new ServiceException(e.getMessage());
		}
		if (solicitacaoExame != null) {
			vo.setCriadoEm(solicitacaoExame.getCriadoEm());
			vo.setSerMatriculaValida(solicitacaoExame.getServidorResponsabilidade() == null ? null : solicitacaoExame.getServidorResponsabilidade().getId().getMatricula());
			vo.setSerVinculoValida(solicitacaoExame.getServidorResponsabilidade() == null ? null : solicitacaoExame.getServidorResponsabilidade().getId().getVinCodigo());
		}
		return vo;
	}

	/**
	 * Chamada para Web Service #38474
	 * Utilizado nas estórias #864 e #27542 
	 * @param atdSeq
	 * @return Boolean
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Boolean verificarExameVDRLnaoSolicitado(Integer atdSeq) throws ApplicationBusinessException{
		return this.solicitacaoExameFacade.verificarExameVDRLnaoSolicitado(atdSeq);
	}
	
	@Override
	public Date obterDataPrimeiraSolicitacaoExamePeloNumConsulta(Integer conNumero) {
		return this.examesFacade.obterDataPrimeiraSolicitacaoExamePeloNumConsulta(conNumero);
	}
	
	/**
	 * Web Service #39251 utilizado na estória #864
	 * @param pacCodigo
	 * @return Boolean
	 */
	@Override
	public Boolean verificaPacienteEmProjetoPesquisa(Integer pacCodigo){
		return this.examesFacade.verificaPacienteEmProjetoPesquisa(pacCodigo);
	}

	@Override
	public List<RegiaoAnatomicaVO> pesquisarRegioesAnatomicasAtivasPorDescricaoSeq(String param) {
		List<RegiaoAnatomicaVO> resultado = new ArrayList<RegiaoAnatomicaVO>();
		List<AelRegiaoAnatomica> lista = this.examesFacade.pesquisarRegioesAnatomicasAtivasPorDescricaoSeq(param);
		for (AelRegiaoAnatomica item : lista) {
			resultado.add(new RegiaoAnatomicaVO(item.getSeq(), item.getDescricao()));
		}
		return resultado;
	}

	@Override
	public Boolean verificarRegioesPorSeqAchadoDescricao(List<Integer> seqs, String descricao) {
		return this.examesFacade.verificarRegioesPorSeqAchadoDescricao(seqs, descricao);
	}

	@Override
	public List<RegiaoAnatomicaVO> buscarRegioesAnatomicas(String descricao) {
		List<RegiaoAnatomicaVO> resultado = new ArrayList<RegiaoAnatomicaVO>();
		List<AelRegiaoAnatomica> lista = this.examesFacade.buscarRegioesAnatomicas(descricao);
		for (AelRegiaoAnatomica item : lista) {
			resultado.add(new RegiaoAnatomicaVO(item.getSeq(), item.getDescricao()));
		}
		return resultado;
	}

	@Override
	public Long pesquisarRegioesAnatomicasAtivasPorDescricaoSeqCount(String param) {		
		return  this.examesFacade.pesquisarRegioesAnatomicasAtivasPorDescricaoSeqCount(param);
	}

	/**
	 *  Serviço para verificar se o atendimento do Recém Nascido possui alguma solicitação de exame. Serviço #42021
	 * @param atdSeq
	 * @param situacoes
	 * @return List<SolicitacaoExamesVO>
	 */
	@Override
	public List<SolicitacaoExamesVO> listarSolicitacaoExamesPorSeqAtdSituacoes(Integer atdSeq, String ... situacoes) throws ServiceException {
		
		try {
			List<AelSolicitacaoExames> list = solicitacaoExameFacade.listarSolicitacaoExamesPorSeqAtdSituacoes(atdSeq, situacoes);
			List<SolicitacaoExamesVO> results = new ArrayList<SolicitacaoExamesVO>();
			
			for (AelSolicitacaoExames item : list) {
				SolicitacaoExamesVO vo =  new SolicitacaoExamesVO(); 
				vo.setSeq(item.getSeq());
				results.add(vo);
			}
			
			return results;
			
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}

	}
	
	@Override
	public RegiaoAnatomicaVO obterRegiaoAnotomicaPorId(Integer seq) {
		AelRegiaoAnatomica entity = this.examesFacade.obterRegiaoAnatomicaPeloId(seq);
		if(entity == null) {
			return null;
		}
		return new RegiaoAnatomicaVO(entity.getSeq(), entity.getDescricao());
	}
	
	public void verificarPermissoesParaSolicitarExame(Integer atendimentoSeq) throws ServiceException {
		try {
			this.solicitacaoExameFacade.verificarPermissoesParaSolicitarExame(atendimentoSeq);
		} catch (BaseException e) {
			throw new ServiceException(messagesUtils.getResourceBundleValue(e));
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
}