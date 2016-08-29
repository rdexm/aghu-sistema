package br.gov.mec.aghu.patrimonio;

import java.util.List;

import br.gov.mec.aghu.sig.custos.vo.EquipamentoProcessamentoMensalVO;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoSistemaPatrimonioVO;
import br.gov.mec.aghu.sig.custos.vo.FolhaPagamentoRHVo;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Interface da Fachada para o modulo de integração do AGHU com sistema de patrimônio do hospital.
 * 
 * @author aghu
 * 
 */
@SuppressWarnings("ucd")
public interface IPatrimonioService {

	/*
	 	Segue abaixo o mapeamento do retorno na consulta do metodo buscaInfoModuloPatrimonio
	 1: significa que o equipamento não foi encontrado no sistema de patrimônio;
	 2: significa que o equipamento foi encontrado no sistema de patrimônio, porém é um bem que não tem controle de depreciação;
	 3: significa que o equipamento foi encontrado no sistema de patrimônio, porém o bem já tem todo o valor depreciado;
	 4: significa que o equipamento foi encontrado no sistema de patrimônio, porém o bem não pertence ao centro de custo da atividade que está sendo cadastrada;
	*/
	public final int RETORNO_INFO_PATRIMONIO_EQUIPAMENTO_NAO_ENCONTRADO = 1;
	public final int RETORNO_INFO_PATRIMONIO_EQUIPAMENTO_SEM_CONTROLE_DEPRECIACAO = 2;
	public final int RETORNO_INFO_PATRIMONIO_EQUIPAMENTO_VALOR_DEPRECIADO = 3;
	public final int RETORNO_INFO_PATRIMONIO_EQUIPAMENTO_PERTENCE_OUTRO_CC = 4;

	EquipamentoSistemaPatrimonioVO buscarInfoModuloPatrimonio(String codigoBem, Integer codigoCentroCusto) throws ApplicationBusinessException;

	List<EquipamentoSistemaPatrimonioVO> pesquisarEquipamentosSistemaPatrimonioById(List<String> listCodigo)	throws ApplicationBusinessException;
	
	List<EquipamentoProcessamentoMensalVO> buscaEquipamentosParaProcessamentoMensal(String dataProcessamento) throws ApplicationBusinessException;

	List<FolhaPagamentoRHVo> buscaFolhaPagamentoMensal(String dataProcessamento) throws ApplicationBusinessException;

	List<EquipamentoSistemaPatrimonioVO> consultaEquipamentoPelaDescricao(String descricao, Integer codigoCentroCusto) throws ApplicationBusinessException;

	List<EquipamentoSistemaPatrimonioVO> pesquisarEquipamentoSistemaPatrimonio(Object paramPesquisa, Integer centroCustoAtividade)
			throws ApplicationBusinessException;

}