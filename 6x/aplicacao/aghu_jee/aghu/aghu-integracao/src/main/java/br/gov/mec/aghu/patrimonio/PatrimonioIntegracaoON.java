package br.gov.mec.aghu.patrimonio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import br.gov.mec.aghu.business.integracao.AGHUIntegracao;
import br.gov.mec.aghu.business.integracao.ServiceEnum;
import br.gov.mec.aghu.business.integracao.exception.AGHUIntegracaoException;
import br.gov.mec.aghu.business.integracao.exception.ESBInacessivelException;
import br.gov.mec.aghu.business.integracao.exception.ExecucaoServicoException;
import br.gov.mec.aghu.business.integracao.exception.ExecucaoServicoNegocioException;
import br.gov.mec.aghu.business.integracao.exception.ServicoIndisponivelException;
import br.gov.mec.aghu.business.integracao.exception.ServicoSemRespostaException;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoProcessamentoMensalVO;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoSistemaPatrimonioVO;
import br.gov.mec.aghu.sig.custos.vo.FolhaPagamentoRHVo;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Deprecated //Deve utilizar o PatrimonioESBService
@Stateless
public class PatrimonioIntegracaoON extends AGHUIntegracao {

	private static final long serialVersionUID = 7020172008920968318L;

	private enum PatrimonioIntegracaoONExceptionCode implements BusinessExceptionCode {
		ESB_INACESSIVEL_EXCEPTION,
		EXECUCAO_SERVICO_EXCEPTION,
		EXECUCAO_SERVICO_NEGOCIO_EXCEPTION,
		SERVICO_INDISPONIVEL_EXCEPTION,
		SERVICO_SEM_RESPOSTA_EXCEPTION,
		ERRO_CHAMADA_SERVICO
	}

	@SuppressWarnings("unchecked")
	public EquipamentoSistemaPatrimonioVO buscarInfoModuloPatrimonio(String codigoBem, Integer codigoCentroCusto) throws ApplicationBusinessException {

		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("codigo-bem", codigoBem);
		parametros.put("codigo-centro-custo", codigoCentroCusto);
		EquipamentoSistemaPatrimonioVO equipamento = null;
		try {
			Object retorno = this.invocarServicoSincrono(ServiceEnum.CONSULTA_EQUIPAMENTO, parametros, null);
			if (retorno instanceof Map<?, ?>) {
				Map<String, Object> mapRetorno = (Map<String, Object>) retorno;
				equipamento = new EquipamentoSistemaPatrimonioVO();

				Object valorRetorno = mapRetorno.get(EquipamentoSistemaPatrimonioVO.Fields.RETORNO.toString());
				if (valorRetorno != null && valorRetorno instanceof BigDecimal) {
					equipamento.setRetorno((BigDecimal) valorRetorno);
				}
				if (equipamento.getRetorno() != null && equipamento.getRetorno().intValue() != 1 && equipamento.getRetorno().intValue() != 4) {
					equipamento.setCodigo(mapRetorno.get(EquipamentoSistemaPatrimonioVO.Fields.CODIGO.toString()).toString());
					equipamento.setConta(mapRetorno.get(EquipamentoSistemaPatrimonioVO.Fields.CONTA.toString()).toString());
					equipamento.setDescricao(mapRetorno.get(EquipamentoSistemaPatrimonioVO.Fields.DESCRICAO.toString()).toString());
					valorRetorno = mapRetorno.get(EquipamentoSistemaPatrimonioVO.Fields.VALOR.toString());
					if (valorRetorno != null && valorRetorno instanceof BigDecimal) {
						equipamento.setValor((BigDecimal) valorRetorno);
					}
					valorRetorno = mapRetorno.get(EquipamentoSistemaPatrimonioVO.Fields.VALORDEPRECIADO.toString());
					if (valorRetorno != null && valorRetorno instanceof BigDecimal) {
						equipamento.setValorDepreciado((BigDecimal) valorRetorno);
					}
				}
				return equipamento;
			} else {
				return equipamento;
			}

		} catch (ESBInacessivelException e) {
			throw new ApplicationBusinessException(PatrimonioIntegracaoONExceptionCode.ESB_INACESSIVEL_EXCEPTION);
		} catch (ExecucaoServicoException e) {
			throw new ApplicationBusinessException(PatrimonioIntegracaoONExceptionCode.EXECUCAO_SERVICO_EXCEPTION);
		} catch (ExecucaoServicoNegocioException e) {
			throw new ApplicationBusinessException(PatrimonioIntegracaoONExceptionCode.EXECUCAO_SERVICO_NEGOCIO_EXCEPTION);
		} catch (ServicoIndisponivelException e) {
			throw new ApplicationBusinessException(PatrimonioIntegracaoONExceptionCode.SERVICO_INDISPONIVEL_EXCEPTION);
		} catch (ServicoSemRespostaException e) {
			throw new ApplicationBusinessException(PatrimonioIntegracaoONExceptionCode.SERVICO_SEM_RESPOSTA_EXCEPTION);
		} catch (AGHUIntegracaoException e) {
			throw new ApplicationBusinessException(PatrimonioIntegracaoONExceptionCode.ERRO_CHAMADA_SERVICO);
		}

	}

	public List<EquipamentoProcessamentoMensalVO> buscaEquipamentosParaProcessamentoMensal(String dataProcessamento) throws ApplicationBusinessException {

		Integer tempoEspera = 10000000;

		Map<String, Object> parametros = new HashMap<String, Object>();
		if (dataProcessamento == null) {
			throw new ApplicationBusinessException(PatrimonioIntegracaoONExceptionCode.EXECUCAO_SERVICO_NEGOCIO_EXCEPTION);
		}
		parametros.put("mes-proc", dataProcessamento);
		List<Object[]> retornoConsulta = this.invocarServicoSincronoLocal(ServiceEnum.BUSCA_EQUIPAMENTOS_DEPRECIADOS_MES, parametros, tempoEspera);
		List<EquipamentoProcessamentoMensalVO> listaEquipamento = new ArrayList<EquipamentoProcessamentoMensalVO>();
		for (Object[] objects : retornoConsulta) {
			EquipamentoProcessamentoMensalVO vo = new EquipamentoProcessamentoMensalVO();
			vo.setCodCentroCusto(Integer.parseInt((String) objects[0]));
			vo.setBem((String) objects[1]);
			vo.setTotalDepreciado((BigDecimal) objects[2]);
			listaEquipamento.add(vo);
		}
		return listaEquipamento;

	}
	
	public List<EquipamentoSistemaPatrimonioVO> buscarEquipamentosPeloID(List<String> listCodigo) throws ApplicationBusinessException{
		
		if(listCodigo == null || listCodigo.isEmpty()){
			return  null;
		}
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("codigo-patrimonio", listCodigo);
		
		List<Object[]> retornoConsulta = this.invocarServicoSincronoLocal(ServiceEnum.CONSULTA_EQUIPAMENTO_BY_ID, parametros, 120000);
		
		List<EquipamentoSistemaPatrimonioVO> list = new ArrayList<EquipamentoSistemaPatrimonioVO>();
		
		for (Object[] objects : retornoConsulta) {
		
			EquipamentoSistemaPatrimonioVO vo = new EquipamentoSistemaPatrimonioVO();
			vo.setConta((String) objects[0]);
			vo.setCodigo((String) objects[1]);
			vo.setDescricao((String) objects[2]);
			vo.setValor((BigDecimal) objects[3]);
			vo.setValorDepreciado((BigDecimal) objects[7]);
			vo.setRetorno(BigDecimal.ONE);
			list.add(vo);
		}
		
		return list;	
	}

	public List<FolhaPagamentoRHVo> buscaFolhaPagamentoMensal(String dataProcessamento) throws ApplicationBusinessException {

		Integer tempoEspera = 10000000;

		Map<String, Object> parametros = new HashMap<String, Object>();
		if (dataProcessamento == null) {
			throw new ApplicationBusinessException(PatrimonioIntegracaoONExceptionCode.EXECUCAO_SERVICO_NEGOCIO_EXCEPTION);
		}
		parametros.put("mes-proc", dataProcessamento);
		List<Object[]> retornoConsulta = this.invocarServicoSincronoLocal(ServiceEnum.BUSCA_FOLHA_PAGAMENTO_MENSAL, parametros, tempoEspera);

		List<FolhaPagamentoRHVo> list = new ArrayList<FolhaPagamentoRHVo>();
		for (Object[] objects : retornoConsulta) {
			FolhaPagamentoRHVo vo = new FolhaPagamentoRHVo();
			vo.setCctCodigoAtua((BigDecimal) objects[0]);
			vo.setCctCodigoLotado((BigDecimal) objects[1]);
			if (objects[2] != null) {
				vo.setGocSeq((BigDecimal) objects[2]);
			}
			if (objects[3] != null) {
				vo.setOcaCarCodigo((String) objects[3]);
			}
			if (objects[4] != null) {
				vo.setOcaCodigo((BigDecimal) objects[4]);
			}
			vo.setNroFuncionarios((BigDecimal) objects[5]);
			vo.setTotHrContrato((BigDecimal) objects[6]);
			vo.setTotHrDesconto((BigDecimal) objects[7]);
			vo.setTotHrExcede((BigDecimal) objects[8]);
			vo.setTotSalBase((BigDecimal) objects[9]);
			vo.setTotSalarios((BigDecimal) objects[10]);
			vo.setTotEncargos((BigDecimal) objects[11]);
			vo.setTotProv13((BigDecimal) objects[12]);
			vo.setTotProvFerias((BigDecimal) objects[13]);
			vo.setTotProvEncargos((BigDecimal) objects[14]);
			list.add(vo);
		}
		return list;

	}

	public List<EquipamentoSistemaPatrimonioVO> consultaEquipamentoPelaDescricao(String descricao, Integer codigoCentroCusto)
			throws ApplicationBusinessException {
		final Integer nroLinhasRetorno = 100; // Retorno da consulta 100 itens
		final Integer tempoEspera = 120000; // 2 mim de serviço

		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("descricao-patrimonio", "%" + descricao + "%");
		parametros.put("nro-linhas-retorno", nroLinhasRetorno);
		parametros.put("codigo-centro-custo", codigoCentroCusto);

		List<Object[]> retornoConsulta = this.invocarServicoSincronoLocal(ServiceEnum.CONSULTA_EQUIPAMENTO_BY_DESCRICAO, parametros, tempoEspera);
		List<EquipamentoSistemaPatrimonioVO> list = new ArrayList<EquipamentoSistemaPatrimonioVO>();

		for (Object[] objects : retornoConsulta) {
			EquipamentoSistemaPatrimonioVO vo = new EquipamentoSistemaPatrimonioVO();
			vo.setConta((String) objects[0]);
			vo.setCodigo((String) objects[1]);
			vo.setDescricao((String) objects[2]);

			vo.setValor((BigDecimal) objects[3]);
			vo.setValorDepreciado((BigDecimal) objects[7]);
			vo.setRetorno(BigDecimal.ONE);
			list.add(vo);
		}

		return list;

	}

	public List<EquipamentoSistemaPatrimonioVO> pesquisarEquipamentoSistemaPatrimonio(Object paramPesquisa, Integer centroCustoAtividade)
			throws ApplicationBusinessException {
		if (centroCustoAtividade == null) {
			centroCustoAtividade = 0;
		}
		if (CoreUtil.isNumeroInteger(paramPesquisa)) {
			return Arrays.asList(this.buscarInfoModuloPatrimonio(paramPesquisa.toString(), centroCustoAtividade));
		} else {
			return this.consultaEquipamentoPelaDescricao(paramPesquisa.toString(), centroCustoAtividade);
		}
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> invocarServicoSincronoLocal(ServiceEnum servico, Map<String, Object> parametros, Integer tempoEspera)
			throws ApplicationBusinessException {
		try {
			return (List<Object[]>) this.invocarServicoSincrono(servico, parametros, tempoEspera);
		} catch (ESBInacessivelException e) {
			throw new ApplicationBusinessException(PatrimonioIntegracaoONExceptionCode.ESB_INACESSIVEL_EXCEPTION);
		} catch (ExecucaoServicoException e) {
			throw new ApplicationBusinessException(PatrimonioIntegracaoONExceptionCode.EXECUCAO_SERVICO_EXCEPTION);
		} catch (ExecucaoServicoNegocioException e) {
			throw new ApplicationBusinessException(PatrimonioIntegracaoONExceptionCode.EXECUCAO_SERVICO_NEGOCIO_EXCEPTION);
		} catch (ServicoIndisponivelException e) {
			throw new ApplicationBusinessException(PatrimonioIntegracaoONExceptionCode.SERVICO_INDISPONIVEL_EXCEPTION);
		} catch (ServicoSemRespostaException e) {
			throw new ApplicationBusinessException(PatrimonioIntegracaoONExceptionCode.SERVICO_SEM_RESPOSTA_EXCEPTION);
		} catch (AGHUIntegracaoException e) {
			throw new ApplicationBusinessException(PatrimonioIntegracaoONExceptionCode.ERRO_CHAMADA_SERVICO);
		}
	}
}
