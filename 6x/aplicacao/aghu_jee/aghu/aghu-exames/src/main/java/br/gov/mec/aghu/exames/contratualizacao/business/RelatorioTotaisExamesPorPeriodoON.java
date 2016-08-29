package br.gov.mec.aghu.exames.contratualizacao.business;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioAgrupamentoTotaisExames;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.contratualizacao.business.GravacaoArquivoXmlRetornoON.GravacaoArquivoXmlRetornoONExceptionCode;
import br.gov.mec.aghu.exames.contratualizacao.vo.TotalItemSolicitacaoExamesVO;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RelatorioTotaisExamesPorPeriodoON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(RelatorioTotaisExamesPorPeriodoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private AelExamesDAO aelExamesDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	private static final long serialVersionUID = -4007537779282839689L;	

	protected enum RealtorioTotaisExamesONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_GERANDO_CSV_COMPLETO, 
		MENSAGEM_ERRO_GERANDO_CSV_SITUACAO, 
		MENSAGEM_ERRO_GERANDO_CSV_DATA, 
		MENSAGEM_ERRO_GERANDO_CSV_SEM_AGRUPAMENTO
	}

	/**
	 * Buscar os dados para realizar a impressão do relatório
	 */
	public List<TotalItemSolicitacaoExamesVO> buscarTotais(Date dataInicial,
			Date dataFinal, Short convenio, Byte plano,
			DominioSituacaoItemSolicitacaoExame situacao, Short unfSeq,
			String sigla, DominioAgrupamentoTotaisExames tipoRelatorio) {

		List<TotalItemSolicitacaoExamesVO> ls = this
				.getAelSolicitacaoExameDAO().buscarTotaisExamesPorPeriodo(
						dataInicial, dataFinal, convenio, plano, situacao,
						unfSeq, sigla, tipoRelatorio);

		if (DominioAgrupamentoTotaisExames.C.equals(tipoRelatorio)) {
			return formatarDadosRelatorioCompleto(ls);
		} else if (DominioAgrupamentoTotaisExames.S.equals(tipoRelatorio)) {
			return formatarDadosRelatorioPorSituacao(ls);
		} else if (DominioAgrupamentoTotaisExames.D.equals(tipoRelatorio)) {
			return formatarDadosRelatorioPorData(ls);
		} else {
			return formatarDadosRelatorio(ls);
		}

	}

	/**
	 * Formatar o VO para a impressão do relatório completo: <li>totalizar as
	 * colunas; <li>limpar as descrições duplicadas;
	 */
	private List<TotalItemSolicitacaoExamesVO> formatarDadosRelatorioCompleto(
			List<TotalItemSolicitacaoExamesVO> listaTotalVO) {

		String unfDescricao = " ";
		String exameDescricao = " ";
		String materialDescricao = " ";
		String dataProgramada = " ";

		Map<String, Integer> map = this.totalizador(listaTotalVO);

		for (TotalItemSolicitacaoExamesVO totalVO : listaTotalVO) {

			if (unfDescricao.equals(totalVO.getUnfDescricao())) {

				if (exameDescricao.equals(totalVO.getExameDescricao())
						&& materialDescricao.equals(totalVO
								.getMaterialAnaliseDescricao())) {

					if (dataProgramada.equals(totalVO.getDataProgramada())) {
						totalVO.setDataProgramada(null);
					} else {
						dataProgramada = totalVO.getDataProgramada();

						if (map.containsKey(totalVO.getUnfDescricao()
								+ totalVO.getExameDescricao()
								+ totalVO.getMaterialAnaliseDescricao()
								+ totalVO.getDataProgramada())) {

							listaTotalVO
									.get(listaTotalVO.indexOf(totalVO))
									.setTotalData(
											map.get(totalVO.getUnfDescricao()
													+ totalVO
															.getExameDescricao()
													+ totalVO
															.getMaterialAnaliseDescricao()
													+ totalVO
															.getDataProgramada()));
						}

					}

					totalVO.setMaterialAnaliseDescricao(null);

				} else {
					exameDescricao = totalVO.getExameDescricao();
					materialDescricao = totalVO.getMaterialAnaliseDescricao();
					dataProgramada = totalVO.getDataProgramada();

					if (map.containsKey(totalVO.getUnfDescricao()
							+ totalVO.getExameDescricao()
							+ totalVO.getMaterialAnaliseDescricao())) {
						listaTotalVO
								.get(listaTotalVO.indexOf(totalVO))
								.setTotalExameMaterial(
										map.get(totalVO.getUnfDescricao()
												+ totalVO.getExameDescricao()
												+ totalVO
														.getMaterialAnaliseDescricao()));
					}

					if (map.containsKey(totalVO.getUnfDescricao()
							+ totalVO.getExameDescricao()
							+ totalVO.getMaterialAnaliseDescricao()
							+ totalVO.getDataProgramada())) {

						listaTotalVO
								.get(listaTotalVO.indexOf(totalVO))
								.setTotalData(
										map.get(totalVO.getUnfDescricao()
												+ totalVO.getExameDescricao()
												+ totalVO
														.getMaterialAnaliseDescricao()
												+ totalVO.getDataProgramada()));
					}

				}

				//totalVO.setExameDescricao(null);
				totalVO.setExameDescricao(exameDescricao);

			} else {
				unfDescricao = totalVO.getUnfDescricao();
				exameDescricao = totalVO.getExameDescricao();
				materialDescricao = totalVO.getMaterialAnaliseDescricao();
				dataProgramada = totalVO.getDataProgramada();

				if (map.containsKey(totalVO.getUnfDescricao())) {
					listaTotalVO.get(listaTotalVO.indexOf(totalVO))
							.setTotalArea(map.get(totalVO.getUnfDescricao()));
				}

				if (map.containsKey(totalVO.getUnfDescricao()
						+ totalVO.getExameDescricao()
						+ totalVO.getMaterialAnaliseDescricao())) {

					listaTotalVO
							.get(listaTotalVO.indexOf(totalVO))
							.setTotalExameMaterial(
									map.get(totalVO.getUnfDescricao()
											+ totalVO.getExameDescricao()
											+ totalVO
													.getMaterialAnaliseDescricao()));
				}

				if (map.containsKey(totalVO.getUnfDescricao()
						+ totalVO.getExameDescricao()
						+ totalVO.getMaterialAnaliseDescricao()
						+ totalVO.getDataProgramada())) {

					listaTotalVO
							.get(listaTotalVO.indexOf(totalVO))
							.setTotalData(
									map.get(totalVO.getUnfDescricao()
											+ totalVO.getExameDescricao()
											+ totalVO
													.getMaterialAnaliseDescricao()
											+ totalVO.getDataProgramada()));
				}

			}

		}

		return listaTotalVO;
	}

	/**
	 * Formatar o VO para a impressão do relatório agrupado pela situação do
	 * exame: <li>totalizar as colunas; <li>limpar as descrições duplicadas;
	 */

	private List<TotalItemSolicitacaoExamesVO> formatarDadosRelatorioPorSituacao(
			List<TotalItemSolicitacaoExamesVO> listaTotalVO) {

		String unfDescricao = " ";
		String exameDescricao = " ";
		String materialDescricao = " ";

		Map<String, Integer> map = this.totalizador(listaTotalVO);

		for (TotalItemSolicitacaoExamesVO totalVO : listaTotalVO) {

			if (unfDescricao.equals(totalVO.getUnfDescricao())) {

				if (exameDescricao.equals(totalVO.getExameDescricao())
						&& materialDescricao.equals(totalVO
								.getMaterialAnaliseDescricao())) {

					totalVO.setMaterialAnaliseDescricao(null);

				} else {
					exameDescricao = totalVO.getExameDescricao();
					materialDescricao = totalVO.getMaterialAnaliseDescricao();

					if (map.containsKey(totalVO.getUnfDescricao()
							+ totalVO.getExameDescricao()
							+ totalVO.getMaterialAnaliseDescricao())) {
						listaTotalVO
								.get(listaTotalVO.indexOf(totalVO))
								.setTotalExameMaterial(
										map.get(totalVO.getUnfDescricao()
												+ totalVO.getExameDescricao()
												+ totalVO
														.getMaterialAnaliseDescricao()));
					}

				}

				//totalVO.setExameDescricao(null);
				totalVO.setExameDescricao(exameDescricao);

			} else {
				unfDescricao = totalVO.getUnfDescricao();
				exameDescricao = totalVO.getExameDescricao();
				materialDescricao = totalVO.getMaterialAnaliseDescricao();

				if (map.containsKey(totalVO.getUnfDescricao())) {
					listaTotalVO.get(listaTotalVO.indexOf(totalVO))
							.setTotalArea(map.get(totalVO.getUnfDescricao()));
				}

				if (map.containsKey(totalVO.getUnfDescricao()
						+ totalVO.getExameDescricao()
						+ totalVO.getMaterialAnaliseDescricao())) {

					listaTotalVO
							.get(listaTotalVO.indexOf(totalVO))
							.setTotalExameMaterial(
									map.get(totalVO.getUnfDescricao()
											+ totalVO.getExameDescricao()
											+ totalVO
													.getMaterialAnaliseDescricao()));
				}

			}

		}

		return listaTotalVO;
	}

	/**
	 * Formatar o VO para a impressão do relatório agrupado pela data do exame:
	 * <li>totalizar as colunas; <li>limpar as descrições duplicadas;
	 */
	private List<TotalItemSolicitacaoExamesVO> formatarDadosRelatorioPorData(
			List<TotalItemSolicitacaoExamesVO> listaTotalVO) {

		String unfDescricao = " ";
		String exameDescricao = " ";
		String materialDescricao = " ";
		String dataProgramada = " ";

		Map<String, Integer> map = this.totalizador(listaTotalVO);

		for (TotalItemSolicitacaoExamesVO totalVO : listaTotalVO) {

			if (unfDescricao.equals(totalVO.getUnfDescricao())) {

				if (exameDescricao.equals(totalVO.getExameDescricao())
						&& materialDescricao.equals(totalVO
								.getMaterialAnaliseDescricao())) {

					if (dataProgramada.equals(totalVO.getDataProgramada())) {
						totalVO.setDataProgramada(null);
					} else {
						dataProgramada = totalVO.getDataProgramada();

						if (map.containsKey(totalVO.getUnfDescricao()
								+ totalVO.getExameDescricao()
								+ totalVO.getMaterialAnaliseDescricao()
								+ totalVO.getDataProgramada())) {

							listaTotalVO
									.get(listaTotalVO.indexOf(totalVO))
									.setTotalData(
											map.get(totalVO.getUnfDescricao()
													+ totalVO
															.getExameDescricao()
													+ totalVO
															.getMaterialAnaliseDescricao()
													+ totalVO
															.getDataProgramada()));
						}

					}

					totalVO.setMaterialAnaliseDescricao(null);

				} else {
					exameDescricao = totalVO.getExameDescricao();
					materialDescricao = totalVO.getMaterialAnaliseDescricao();
					dataProgramada = totalVO.getDataProgramada();

					if (map.containsKey(totalVO.getUnfDescricao()
							+ totalVO.getExameDescricao()
							+ totalVO.getMaterialAnaliseDescricao())) {
						listaTotalVO
								.get(listaTotalVO.indexOf(totalVO))
								.setTotalExameMaterial(
										map.get(totalVO.getUnfDescricao()
												+ totalVO.getExameDescricao()
												+ totalVO
														.getMaterialAnaliseDescricao()));
					}

					if (map.containsKey(totalVO.getUnfDescricao()
							+ totalVO.getExameDescricao()
							+ totalVO.getMaterialAnaliseDescricao()
							+ totalVO.getDataProgramada())) {

						listaTotalVO
								.get(listaTotalVO.indexOf(totalVO))
								.setTotalData(
										map.get(totalVO.getUnfDescricao()
												+ totalVO.getExameDescricao()
												+ totalVO
														.getMaterialAnaliseDescricao()
												+ totalVO.getDataProgramada()));
					}

				}

				//totalVO.setExameDescricao(null);
				totalVO.setExameDescricao(exameDescricao);

			} else {
				unfDescricao = totalVO.getUnfDescricao();
				exameDescricao = totalVO.getExameDescricao();
				materialDescricao = totalVO.getMaterialAnaliseDescricao();
				dataProgramada = totalVO.getDataProgramada();

				if (map.containsKey(totalVO.getUnfDescricao())) {
					listaTotalVO.get(listaTotalVO.indexOf(totalVO))
							.setTotalArea(map.get(totalVO.getUnfDescricao()));
				}

				if (map.containsKey(totalVO.getUnfDescricao()
						+ totalVO.getExameDescricao()
						+ totalVO.getMaterialAnaliseDescricao())) {

					listaTotalVO
							.get(listaTotalVO.indexOf(totalVO))
							.setTotalExameMaterial(
									map.get(totalVO.getUnfDescricao()
											+ totalVO.getExameDescricao()
											+ totalVO
													.getMaterialAnaliseDescricao()));
				}

				if (map.containsKey(totalVO.getUnfDescricao()
						+ totalVO.getExameDescricao()
						+ totalVO.getMaterialAnaliseDescricao()
						+ totalVO.getDataProgramada())) {

					listaTotalVO
							.get(listaTotalVO.indexOf(totalVO))
							.setTotalData(
									map.get(totalVO.getUnfDescricao()
											+ totalVO.getExameDescricao()
											+ totalVO
													.getMaterialAnaliseDescricao()
											+ totalVO.getDataProgramada()));
				}

			}

		}

		return listaTotalVO;
	}

	/**
	 * Formatar o VO para a impressão do relatório: <li>totalizar as colunas;
	 * <li>limpar as descrições duplicadas;
	 */
	private List<TotalItemSolicitacaoExamesVO> formatarDadosRelatorio(List<TotalItemSolicitacaoExamesVO> listaTotalVO) {

		String unfDescricao = " ";
		String exameDescricao = " ";
		String materialDescricao = " ";

		Map<String, Integer> map = this.totalizador(listaTotalVO);

		for (TotalItemSolicitacaoExamesVO totalVO : listaTotalVO) {

			if (unfDescricao.equals(totalVO.getUnfDescricao())) {

				if (exameDescricao.equals(totalVO.getExameDescricao())
						&& materialDescricao.equals(totalVO
								.getMaterialAnaliseDescricao())) {

					totalVO.setMaterialAnaliseDescricao(null);

				} else {
					exameDescricao = totalVO.getExameDescricao();
					materialDescricao = totalVO.getMaterialAnaliseDescricao();

					if (map.containsKey(totalVO.getUnfDescricao()
							+ totalVO.getExameDescricao()
							+ totalVO.getMaterialAnaliseDescricao())) {
						listaTotalVO
								.get(listaTotalVO.indexOf(totalVO))
								.setTotalExameMaterial(
										map.get(totalVO.getUnfDescricao()
												+ totalVO.getExameDescricao()
												+ totalVO
														.getMaterialAnaliseDescricao()));
					}

				}

				//totalVO.setExameDescricao(null);
				totalVO.setExameDescricao(exameDescricao);

			} else {
				unfDescricao = totalVO.getUnfDescricao();
				exameDescricao = totalVO.getExameDescricao();
				materialDescricao = totalVO.getMaterialAnaliseDescricao();

				if (map.containsKey(totalVO.getUnfDescricao())) {
					listaTotalVO.get(listaTotalVO.indexOf(totalVO))
							.setTotalArea(map.get(totalVO.getUnfDescricao()));
				}

				if (map.containsKey(totalVO.getUnfDescricao()
						+ totalVO.getExameDescricao()
						+ totalVO.getMaterialAnaliseDescricao())) {

					listaTotalVO
							.get(listaTotalVO.indexOf(totalVO))
							.setTotalExameMaterial(
									map.get(totalVO.getUnfDescricao()
											+ totalVO.getExameDescricao()
											+ totalVO
													.getMaterialAnaliseDescricao()));
				}

			}

		}

		return listaTotalVO;
	}

	/**
	 * Carregar um MAP<STRING,INTEGER> com os totalizadores abaixo <li>Unidade;
	 * <li>Exame + Material Análise; <li>Data;
	 */
	private Map<String, Integer> totalizador( List<TotalItemSolicitacaoExamesVO> list) {

		Map<String, Integer> mapRetorno = new HashMap<String, Integer>();
		Integer total = 0;

		for (TotalItemSolicitacaoExamesVO vo : list) {

			if (vo.getUnfDescricao() != null
					&& mapRetorno.containsKey(vo.getUnfDescricao())) {
				total = mapRetorno.get(vo.getUnfDescricao());
				mapRetorno.put(vo.getUnfDescricao(),
						total += vo.getTotalSituacao());
			} else {
				mapRetorno.put(vo.getUnfDescricao(), vo.getTotalSituacao());
			}

			if (vo.getUnfDescricao() != null
					&& vo.getExameDescricao() != null
					&& vo.getMaterialAnaliseDescricao() != null
					&& mapRetorno.containsKey(vo.getUnfDescricao()
							+ vo.getExameDescricao()
							+ vo.getMaterialAnaliseDescricao())) {
				total = mapRetorno.get(vo.getUnfDescricao()
						+ vo.getExameDescricao()
						+ vo.getMaterialAnaliseDescricao());
				mapRetorno.put(vo.getUnfDescricao() + vo.getExameDescricao()
						+ vo.getMaterialAnaliseDescricao(),
						total += vo.getTotalSituacao());
			} else {
				mapRetorno.put(vo.getUnfDescricao() + vo.getExameDescricao()
						+ vo.getMaterialAnaliseDescricao(),
						vo.getTotalSituacao());
			}

			if (vo.getUnfDescricao() != null
					&& vo.getExameDescricao() != null
					&& vo.getMaterialAnaliseDescricao() != null
					&& vo.getDataProgramada() != null
					&& mapRetorno.containsKey(vo.getUnfDescricao()
							+ vo.getExameDescricao()
							+ vo.getMaterialAnaliseDescricao()
							+ vo.getDataProgramada())) {
				total = mapRetorno.get(vo.getUnfDescricao()
						+ vo.getExameDescricao()
						+ vo.getMaterialAnaliseDescricao()
						+ vo.getDataProgramada());
				mapRetorno.put(
						vo.getUnfDescricao() + vo.getExameDescricao()
								+ vo.getMaterialAnaliseDescricao()
								+ vo.getDataProgramada(),
						total += vo.getTotalSituacao());
			} else {
				mapRetorno
						.put(vo.getUnfDescricao() + vo.getExameDescricao()
								+ vo.getMaterialAnaliseDescricao()
								+ vo.getDataProgramada(), vo.getTotalSituacao());
			}

		}

		return mapRetorno;
	}

	public void gerarCSV(Date dataInicial, Date dataFinal, Short convenio,
			Byte plano, DominioSituacaoItemSolicitacaoExame situacao,
			Short unfSeq, String sigla,
			DominioAgrupamentoTotaisExames tipoRelatorio)
			throws ApplicationBusinessException, BaseException,
			IOException{

		List<TotalItemSolicitacaoExamesVO> totais = this.buscarTotais(
				dataInicial, dataFinal, convenio, plano, situacao, unfSeq,
				sigla, tipoRelatorio);

		if (tipoRelatorio.equals(DominioAgrupamentoTotaisExames.C)) {
			this.geraCSVCompleto(totais, dataInicial, dataFinal);
		} else if (tipoRelatorio.equals(DominioAgrupamentoTotaisExames.D)) {
			this.geraCSVDataProgramada(totais, dataInicial, dataFinal);
		} else if (tipoRelatorio.equals(DominioAgrupamentoTotaisExames.S)) {
			this.geraCSVSituacao(totais, dataInicial, dataFinal);
		} else if (tipoRelatorio.equals(DominioAgrupamentoTotaisExames.X)) {
			this.geraCSVsemAgrupamento(totais, dataInicial, dataFinal);
		}

	}

	private void geraCSVsemAgrupamento(List<TotalItemSolicitacaoExamesVO> totais, Date dataInicial, Date dataFinal)
			throws ApplicationBusinessException{
		try {
			StringBuilder builder = new StringBuilder(110);
			// coloca cabecalho na string buffer
			builder.append("EXAME;DESCRICAO_UNIDADE_FUNCIONAL;MATERIAL_ANALISE_DESCRICAO;TOTAL_EXAME_MATERIAL;TOTAL_AREA;\n")
			.append(incluiPeriodoDePesquisaNoCSV( dataInicial, dataFinal));
			
			// inclui as linhas na string buffer
			for (TotalItemSolicitacaoExamesVO item : totais) {

				builder.append(item.getExameDescricao())
				.append("; ")
				.append(item.getUnfDescricao())
				.append("; ")
				.append(item.getMaterialAnaliseDescricao())
				.append("; ")
				.append(item.getTotalExameMaterial())
				.append("; ")
				.append(item.getTotalArea())
				.append('\n');
			}

			File arq = new File(buscarCaminhoGravacao()
					+ DominioNomeRelatorio.RELATORIO_TOTAIS_EXAMES_POR_PERIODO
					+ DominioNomeRelatorio.EXTENSAO_CSV);

			BufferedOutputStream arquivo = new BufferedOutputStream(
					new FileOutputStream(arq));

			// grava a string buffer no arquivo no local definido
			arquivo.write(builder.toString().getBytes());
			arquivo.close();

		} catch (BaseException e) {			
			throw new ApplicationBusinessException(
					RealtorioTotaisExamesONExceptionCode.MENSAGEM_ERRO_GERANDO_CSV_SEM_AGRUPAMENTO);

		} catch (Exception e) {			
			logError("Exceção capturada: ", e);
			this.logError(e.getMessage());			
		}

	}

	private void geraCSVSituacao(List<TotalItemSolicitacaoExamesVO> totais, Date dataInicial, Date dataFinal)
			throws ApplicationBusinessException, IOException {
		try {
			StringBuilder builder = new StringBuilder(140);
			// coloca cabecalho na string buffer
			builder.append("EXAME;DESCRICAO_UNIDADE_FUNCIONAL;MATERIAL_ANALISE_DESCRICAO;COD_SITUACAO;TOTAL_SITUACAO;TOTAL_EXAME_MATERIAL;TOTAL_AREA;\n")
			.append(incluiPeriodoDePesquisaNoCSV( dataInicial, dataFinal));
			
			// inclui as linhas na string buffer
			for (TotalItemSolicitacaoExamesVO item : totais) {

				builder.append(item.getExameDescricao())
				.append("; ")
				.append(item.getUnfDescricao())
				.append("; ")
				.append(item.getMaterialAnaliseDescricao())
				.append("; ")
				.append(item.getSituacaoCodigo())
				.append("; ")
				.append(item.getTotalSituacao())
				.append("; ")
				.append(item.getTotalExameMaterial())
				.append("; ")
				.append(item.getTotalArea())
				.append("; ")
				.append('\n');
			}

			File arq = new File(buscarCaminhoGravacao()
					+ DominioNomeRelatorio.RELATORIO_TOTAIS_EXAMES_POR_PERIODO
					+ DominioNomeRelatorio.EXTENSAO_CSV);

			BufferedOutputStream arquivo = new BufferedOutputStream(
					new FileOutputStream(arq));

			// grava a string buffer no arquivo no local definido
			arquivo.write(builder.toString().getBytes());
			arquivo.close();

		} catch (BaseException e) {

			throw new ApplicationBusinessException(
					RealtorioTotaisExamesONExceptionCode.MENSAGEM_ERRO_GERANDO_CSV_SITUACAO);

		} 
	}

	private void geraCSVDataProgramada(List<TotalItemSolicitacaoExamesVO> totais, Date dataInicial, Date dataFinal)
			throws ApplicationBusinessException, IOException{
		try {
			StringBuilder builder = new StringBuilder(130);
			// coloca cabecalho na string buffer
			builder.append("EXAME;DESCRICAO_UNIDADE_FUNCIONAL;MATERIAL_ANALISE_DESCRICAO;DATA;TOTAL_DATA;TOTAL_EXAME_MATERIAL;TOTAL_AREA;\n")
			.append(incluiPeriodoDePesquisaNoCSV( dataInicial, dataFinal));
			
			// inclui as linhas na string buffer
			for (TotalItemSolicitacaoExamesVO item : totais) {

				builder.append(item.getExameDescricao())
				.append("; ")
				.append(item.getUnfDescricao())
				.append("; ")
				.append(item.getMaterialAnaliseDescricao())
				.append("; ")
				.append(item.getDataProgramada())
				.append("; ")
				.append(item.getTotalData())
				.append("; ")
				.append(item.getTotalExameMaterial())
				.append("; ")
				.append(item.getTotalArea())
				.append("; ")
				.append('\n');
			}

			File arq = new File(buscarCaminhoGravacao()
					+ DominioNomeRelatorio.RELATORIO_TOTAIS_EXAMES_POR_PERIODO
					+ DominioNomeRelatorio.EXTENSAO_CSV);

			BufferedOutputStream arquivo = new BufferedOutputStream(
					new FileOutputStream(arq));

			// grava a string buffer no arquivo no local definido
			arquivo.write(builder.toString().getBytes());
			arquivo.close();

		} catch (BaseException e) {

			throw new ApplicationBusinessException(
					RealtorioTotaisExamesONExceptionCode.MENSAGEM_ERRO_GERANDO_CSV_DATA);

		}

	}

	private void geraCSVCompleto(List<TotalItemSolicitacaoExamesVO> totais, Date dataInicial, Date dataFinal)
			throws ApplicationBusinessException, IOException {
		try {
			StringBuilder builder = new StringBuilder(160);
			// coloca cabecalho na string buffer
			builder.append("EXAME;DESCRICAO_UNIDADE_FUNCIONAL;MATERIAL_ANALISE_DESCRICAO;DATA;COD_SITUACAO;TOTAL_SITUACAO;TOTAL_DATA;TOTAL_EXAME_MATERIAL;TOTAL_AREA;\n")
			.append(incluiPeriodoDePesquisaNoCSV( dataInicial, dataFinal));

			// inclui as linhas na string buffer
			for (TotalItemSolicitacaoExamesVO item : totais) {

				builder.append(item.getExameDescricao())
				.append("; ")
				.append(item.getUnfDescricao())
				.append("; ")
				.append(item.getMaterialAnaliseDescricao())
				.append("; ")
				.append(item.getDataProgramada())
				.append("; ")
				.append(item.getSituacaoCodigo())
				.append("; ")
				.append(item.getTotalSituacao())
				.append("; ")
				.append(item.getTotalData())
				.append("; ")
				.append(item.getTotalExameMaterial())
				.append("; ")
				.append(item.getTotalArea())
				.append("; ")
				.append('\n');
			}

			File arq = new File(buscarCaminhoGravacao()
					+ DominioNomeRelatorio.RELATORIO_TOTAIS_EXAMES_POR_PERIODO
					+ DominioNomeRelatorio.EXTENSAO_CSV);

			BufferedOutputStream arquivo = new BufferedOutputStream(
					new FileOutputStream(arq));

			// grava a string buffer no arquivo no local definido
			arquivo.write(builder.toString().getBytes());
			arquivo.close();

		} catch (BaseException e) {

			throw new ApplicationBusinessException(
					RealtorioTotaisExamesONExceptionCode.MENSAGEM_ERRO_GERANDO_CSV_COMPLETO);

		} 
	}
	
	private String incluiPeriodoDePesquisaNoCSV(Date dataInicial, Date dataFinal) {

		String periodo = "\n" + " " + dataInicial + " até " + dataFinal + "\n";

		return periodo;
	}

	// Busca o caminho de gravacao do arquivo no parametro definido
	private String buscarCaminhoGravacao()
			throws ApplicationBusinessException {

		AghParametros parametro = getParametroFacade().obterAghParametro(
				AghuParametrosEnum.P_DIR_EXAMES_CONTRATUALIZACAO_CSV);

		if (parametro == null || parametro.getVlrTexto() == null) {
			throw new ApplicationBusinessException(
					GravacaoArquivoXmlRetornoONExceptionCode.MENSAGEM_VALOR_PARAMETRO_NAO_ENCONTRADO,
					AghuParametrosEnum.P_DIR_EXAMES_CONTRATUALIZACAO_CSV.name());
		}

		return parametro.getVlrTexto();
	}

	private IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}

	public List<FatConvenioSaudePlano> sbObterConvenio(Object parametro) {
		return this.getFaturamentoFacade().sbObterConvenio(parametro);		
	}

	public Long sbObterConvenioCount(Object parametro) {
		return this.getFaturamentoFacade().sbObterConvenioCount(
				parametro);
	}

	public List<AelExames> sbObterExame(Object parametro) {
		return this.getAelExamesDAO().sbBuscarExames(parametro);
	}

	public Long sbObterExameCount(Object parametro) {
		return this.getAelExamesDAO().sbBuscarExamesCount(parametro);
	}

	public List<AelSitItemSolicitacoes> obterListaSituacoesItemSolicitacoes() {
		return this.getAelSolicitacaoExameDAO()
				.obterListaSituacoesItemSolicitacoes();
	}
	
	private IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}
	
	private AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}

	private AelExamesDAO getAelExamesDAO() {
		return aelExamesDAO;
	}

}
