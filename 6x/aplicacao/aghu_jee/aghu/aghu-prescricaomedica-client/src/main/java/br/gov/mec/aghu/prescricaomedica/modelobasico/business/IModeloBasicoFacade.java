package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmCuidadoUsualUnf;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidado;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidadoId;
import br.gov.mec.aghu.model.MpmModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamentoId;
import br.gov.mec.aghu.model.MpmModeloBasicoModoUsoProcedimento;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimento;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.modelobasico.vo.ItensModeloBasicoVO;
import br.gov.mec.aghu.prescricaomedica.modelobasico.vo.MpmItemModeloBasicoDietaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IModeloBasicoFacade extends Serializable {

	/**
	 * Retorna lista de itens do modelo
	 * 
	 * @param seqModelo
	 * @return
	 */
	List<ItensModeloBasicoVO> obterListaItensModelo(Integer seqModelo);

	/**
	 * Retorna lista de itens de determinado modelo de dieta
	 * 
	 * @param modeloBasicoPrescricaoSeq
	 * @param modeloBasicoDietaSeq
	 * @return
	 */
	List<MpmItemModeloBasicoDieta> obterListaItensDieta(
			Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoDietaSeq);
	
	/**
	 * Retorna lista de itens de determinado modelo de dieta
	 * 
	 * @param modeloBasicoPrescricaoSeq
	 * @param modeloBasicoDietaSeq
	 * @return
	 */
	List<MpmItemModeloBasicoDietaVO> obterListaItensDietaVO(
			Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoDietaSeq);

	/**
	 * Retorna lista de cuidados de determinado modelo de cuidado
	 * 
	 */
	List<MpmModeloBasicoCuidado> obterListaCuidados(
			Integer modeloBasicoPrescricaoSeq);

	Long obterListaCuidadoUsualCount(Object cuidadoUsualPesquisa);

	Long obterListaTipoFrequenciaAprazamentoCount(String strPesquisa);
	
	/**
	 * Retorna a descrição editada de um modelo de cuidado
	 * @param modeloBasicoPrescricaoSeq
	 * @param seq
	 * @return
	 */
	String obterDescricaoEditadaModeloBasicoCuidado(final Integer modeloBasicoPrescricaoSeq, final Integer seq);
	
	/**
	 * Retorna a descrição editada de um modelo de cuidado
	 * @param modeloBasicoCuidado
	 * @return
	 */
	String obterDescricaoEditadaModeloBasicoCuidado(MpmModeloBasicoCuidado modeloBasicoCuidado);
	
	
	/**
	 * Retorna modelo básico pelo id.
	 * 
	 * @param seqModelo
	 * @return
	 */
	MpmModeloBasicoPrescricao obterModeloBasico(Integer seqModelo);
	
	MpmModeloBasicoPrescricao obterModeloBasicoPrescricaoComServidorPorId(
			Integer seq) throws ApplicationBusinessException;

	MpmCuidadoUsual obterCuidadoUsual(Integer seq);

	/**
	 * Inclui um novo item de dieta do modelo básico
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	void inserir(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException;

	/**
	 * Altera um modelo de dieta Existente
	 * 
	 * @param dieta
	 * @throws ApplicationBusinessException
	 */
	void alterar(MpmModeloBasicoDieta dieta) throws ApplicationBusinessException;

	/**
	 * Altera um modelo de cuidado Existente
	 * 
	 * @param dieta
	 * @throws ApplicationBusinessException
	 */
	void alterar(MpmModeloBasicoCuidado modeloBasicoCuidado)
			throws ApplicationBusinessException;

	MpmModeloBasicoDieta obterModeloBasicoDieta(
			Integer modeloBasicoPrescricaoSeq, Integer seq);

	MpmModeloBasicoCuidado obterModeloBasicoCuidado(
			Integer modeloBasicoPrescricaoSeq, Integer seq);

	Long obterTiposItemDietaCount(Object idOuDescricao);

	MpmModeloBasicoMedicamento obterModeloBasicoMedicamento(Integer seqModelo,
			Integer seqItemModelo);
	
	MpmModeloBasicoMedicamento obterModeloBasicoMedicamento(MpmModeloBasicoMedicamentoId id, boolean left, Enum ...fields);

	MpmModeloBasicoProcedimento obterModeloBasicoProcedimento(
			Integer seqModelo, Integer seqItemModelo);

	/**
	 * Lista os tipos de frequencia de aprazamentos cadastrados
	 * 
	 * @return lista TipoFrequenciaAprazamento
	 */
	List<MpmTipoFrequenciaAprazamento> obterListaTipoFrequenciaAprazamento(
			String strPesquisa);

	/**
	 * Retorna os tipos de itens de dieta ativos.
	 * 
	 * @param descricaoOuId
	 *            pode ser a descrição ou o id, se null lista todos ativos
	 * @return
	 */
	List<AnuTipoItemDieta> obterTiposItemDieta(Object descricaoOuId);

	/**
	 * Retorna os modelos básicos públicos
	 * 
	 * @param descricaoModelo
	 * @param descricaoCentroCusto
	 * @return
	 */
	List<MpmModeloBasicoPrescricao> pequisarModelosPublicos(
			String descricaoModelo, String descricaoCentroCusto,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	/**
	 * Retorna quantidade de modelos básicos públicos
	 * 
	 * @return
	 */
	Long pequisarModelosPublicosCount(String descricaoModelo, String descricaoCentroCusto);

	void inserir(MpmModeloBasicoPrescricao modeloBasico)
			throws ApplicationBusinessException;

	void inserir(MpmModeloBasicoCuidado modeloBasicoCuidado)
			throws BaseException;

	void inserir(
			MpmModeloBasicoProcedimento modeloBasicoProcedimento,
			List<MpmModeloBasicoModoUsoProcedimento> listaModoUsoProdedimentoEspecial)
			throws BaseException;

	void alterar(MpmModeloBasicoPrescricao modeloBasico)
			throws ApplicationBusinessException;

	void excluirModeloBasico(Integer modeloBasicoSeq) throws ApplicationBusinessException;

	void excluir(Object object) throws BaseException;

	/**
	 * Altera um item de dieta do modelo básico
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	void alterar(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException;

	/**
	 * Exclui um item de dieta do modelo básico
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	void excluir(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException;

	/**
	 * Busca Item do modelo de dieta pelo id
	 * 
	 * @param modeloBasicoPrescricaoSeq
	 * @param modeloBasicoDietaSeq
	 * @param tipoItemDietaSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	MpmItemModeloBasicoDieta obterItemDieta(Integer modeloBasicoPrescricaoSeq,
			Integer modeloBasicoDietaSeq, Integer tipoItemDietaSeq)
			throws ApplicationBusinessException;

	boolean isAlterouDieta(MpmModeloBasicoDieta dieta);

	List<MpmModeloBasicoProcedimento> obterListaProcedimentos(
			MpmModeloBasicoPrescricao modeloBasicoPrescricao);

	List<MpmModeloBasicoModoUsoProcedimento> obterListaModoDeUsoDoModelo(
			MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento)
			throws ApplicationBusinessException;

	/**
	 * Escolher ítens do modelo basico - carga modelo básico
	 */
	Boolean incluirItensSelecionados(MpmPrescricaoMedica prescricaoMedica,
			List<ItensModeloBasicoVO> itens, String nomeMicrocomputador) throws BaseException;

	/**
	 * Retorna descrição concatenando os itens desta dieta.
	 * 
	 * @param dieta
	 * @return String
	 */
	String getDescricaoEditadaDieta(MpmModeloBasicoDieta dieta);
	
	/**
	 * Retorna descrição concatenando os medicamentos.
	 * 
	 * @param dieta
	 * @return String
	 */
	String getDescricaoEditadaMedicamento(MpmModeloBasicoMedicamento medicamento);


	/**
	 * Retorna descrição concatenando os itens de medicamentos/solução
	 * 
	 * @param medicamento
	 * @return String
	 */
	String getDescricaoEditadaMedicamentoItem(MpmModeloBasicoMedicamento medicamento);

	/**
	 * Retorna descrição editada de procedimentos
	 * 
	 * @param procedimento
	 * @return
	 */
	String getDescricaoEditadaProcedimento(
			MpmModeloBasicoProcedimento procedimento);

	MpmModeloBasicoCuidado obterItemCuidado(MpmModeloBasicoCuidadoId id);

	/**
	 * Retorna lista de cuidados usuais ativos com a descrição/seq
	 * 
	 * @param cuidadoUsualPesquisa
	 * @return
	 */
	List<MpmCuidadoUsual> obterListaCuidadosUsuais(Object cuidadoUsualPesquisa);

	List<MpmModeloBasicoMedicamento> obterListaSolucoesDoModeloBasico(
			MpmModeloBasicoPrescricao modeloBasico);

	/**
	 * Retorna Modelo Basico de Medicamento pelo id.<br>
	 * Faz refresh.
	 * 
	 * @param seqModelo
	 * @param seqItemModelo
	 * @return
	 */
	MpmModeloBasicoMedicamento obterModeloBasicoSolucao(Integer seqModelo,
			Integer seqItemModelo);

	/**
	 * Lista de Itens de Medicamentos
	 * 
	 * @param modeloBasicoPrescricaoSeq
	 * @return
	 */
	List<MpmModeloBasicoMedicamento> obterListaMedicamentosModelo(
			Integer modeloBasicoPrescricaoSeq);

	/**
	 * Inclui um novo item de modelo básico de medicamento com seu pai
	 * 
	 * @param itemMedicamento
	 * @throws ApplicationBusinessException
	 */
	void inserir(MpmItemModeloBasicoMedicamento itemMedicamento)
			throws ApplicationBusinessException;

	/**
	 * Altera as informações do modelo de Medicamento
	 * 
	 * @param modeloMedicamento
	 * @throws ApplicationBusinessException
	 */
	void alterar(MpmItemModeloBasicoMedicamento itemMedicamento)
			throws ApplicationBusinessException;

	List<MpmItemModeloBasicoMedicamento> obterItemMedicamento(
			Integer modeloBasicoPrescricaoSeq,
			Integer modeloBasicoMedicamentoSeq) throws ApplicationBusinessException;

	String gravarSolucao(MpmModeloBasicoMedicamento solucao,
			List<MpmItemModeloBasicoMedicamento> itensModeloMedicamento, List<MpmItemModeloBasicoMedicamento> listaExcluidos)
			throws BaseException;

	void alterar(
			MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento,
			List<MpmModeloBasicoModoUsoProcedimento> mpmModeloBasicoModoUsoProcedimentos)
			throws BaseException;

	List<AghUnidadesFuncionais> getListaUnidadesFuncionais(String paramString)
			throws ApplicationBusinessException;

	List<AghUnidadesFuncionais> getListaUnidadesFuncionais(
			RapServidores servidor) throws ApplicationBusinessException;

	Long pesquisarCuidadoUsualCount(Integer codigoPesquisaCuidadoUsual,
			String descricaoPesquisaCuidadoUsual,
			DominioSituacao situacaoPesquisaCuidadoUsual,
			AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual);

	List<MpmCuidadoUsual> pesquisarCuidadoUsual(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigoPesquisaCuidadoUsual,
			String descricaoPesquisaCuidadoUsual,
			DominioSituacao situacaoPesquisaCuidadoUsual,
			AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual);

	void excluir(Integer seqCuidado) throws ApplicationBusinessException;

	void copiarModeloBasico(Integer seq) throws BaseException;

	void alterar(MpmCuidadoUsual cuidadoUsual,List<Short> ufsInseridas,  List<Short> ufsExcluidas) throws ApplicationBusinessException;

	void inserir(MpmCuidadoUsual cuidadoUsual,List<Short> ufsInseridas) throws ApplicationBusinessException;

	List<MpmCuidadoUsualUnf> listaUnidadeFuncionalPorCuidadoUsual() throws ApplicationBusinessException;
	
	List<MpmCuidadoUsualUnf> listaUnidadeFuncionalPorCuidadoUsual( Integer seqCuidado) throws ApplicationBusinessException;
	
	/**
	 * Retorna o numero total de pagina.
	 * 
	 * @param codigoPesquisaCuidadoUsual
	 * @param descricaoPesquisaCuidadoUsual
	 * @param situacaoPesquisaCuidadoUsual
	 * @param unidadeFuncionalPesquisaCuidadoUsual
	 * @throws AGHUNegocioException
	 */
	Integer pesquisarCuidadoUsualUnfCount(AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual);

	/**
	 * Retorna a pesquisa de cuidados com sua respetiva unidades.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param codigoPesquisaCuidadoUsual
	 * @param situacaoPesquisaCuidadoUsual
	 * @param unidadeFuncionalPesquisaCuidadoUsual
	 * @throws AGHUNegocioException
	 */
	List<MpmCuidadoUsualUnf> pesquisarCuidadoUsualUnf(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual);
	
	void removerSolucoesSelecionadas( MpmModeloBasicoMedicamento modeloBasicoMedicamento) throws ApplicationBusinessException;
	
	void incluirCuidadosUnf(AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual, RapServidores rapServidores) throws ApplicationBusinessException ;

	void incluirTodosCuidadosUnf(RapServidores rapServidores) throws ApplicationBusinessException;

	List<MpmModeloBasicoPrescricao> listarModelosBasicos();
	
	String getDescricaoEditadaModeloBasicoProcedimento(MpmModeloBasicoProcedimento procedimento);
	
	MpmTipoFrequenciaAprazamento obterTipoFrequenciaAprazamento(Short seq);

    void removeListaModoDeUsoDoModelo(MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento) throws ApplicationBusinessException;

    void excluir(MpmCuidadoUsualUnf mpmCuidadoUsualUnf, RapServidores servidorLogado) throws ApplicationBusinessException;
}