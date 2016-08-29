package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.model.RapCargos;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapGrupoFuncional;
import br.gov.mec.aghu.model.RapInstituicaoQualificadora;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapOcupacoesCargoId;
import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.model.RapTipoAfastamento;
import br.gov.mec.aghu.model.RapTipoAtuacao;
import br.gov.mec.aghu.model.RapTipoInformacoes;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.registrocolaborador.vo.OcupacaoCargoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface ICadastrosBasicosFacade extends Serializable {

	// Vinculo
	public RapVinculos obterVinculo(Short codigoVinculo)
			throws ApplicationBusinessException;

	public Long pesquisarVinculosCount(Short codigo, String descricao,
			DominioSituacao indSituacao);

	public List<RapVinculos> pesquisarVinculos(Short codigo, String descricao,
			DominioSituacao indSituacao, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);

	void excluirVinculo(Short codigoVinculo) throws ApplicationBusinessException;

	/**
	 * PopulaTabela tabela RAP_COD_STARH_LIVRES, caso esteja vazia
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void populaTabela() throws ApplicationBusinessException;

	/**
	 * @param codigoVinculo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public RapVinculos buscarVinculos(Short codigoVinculo, boolean somenteAtivo)
			throws ApplicationBusinessException;

	/**
	 * Retorna vínculos de acrodo com o código ou descrição informados por ordem
	 * de codigo
	 * 
	 * @param vinculo
	 *            ou descricao
	 * @return vínculos encontrados lista vazia se não encontrado
	 */
	public List<RapVinculos> pesquisarVinculoPorCodigoDescricao(Object vinculo,
			boolean somenteAtivo) throws ApplicationBusinessException;

	// Tipo Informações
	public RapTipoInformacoes obterTipoInformacoes(Integer TipoInformacoesCodigo)
			throws ApplicationBusinessException;

	public void persistirTipoInformacoes(RapTipoInformacoes rapTipoInformacoes)
			throws ApplicationBusinessException;

	public List<RapTipoInformacoes> pesquisarTipoInformacoesSuggestion(
			Object objPesquisa);

	public Long pesquisarTipoInformacoesSuggestionCount(Object objPesquisa);
	
	List<Integer> pesquisarTipoInformacoesPorDescricao(String descricao);

	public Long obterTipoInformacoesCount(Integer codigo, String descricao);

	public List<RapTipoInformacoes> pesquisarTipoInformacoes(
			Integer firstResult, Integer maxResults, Integer codigo,
			String descricao);

	public void excluirTipoInformacoes(Integer tipoInformacoesCodigo)
			throws ApplicationBusinessException;

	/**
	 * Retorna instância pelo codigo da ocupação.
	 * 
	 * @param codigoOcupacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public RapOcupacaoCargo obterOcupacaoCargoPorCodigo(Integer codigoOcupacao,
			boolean somenteAtivo) throws ApplicationBusinessException;

	public List<RapOcupacaoCargo> pesquisarOcupacaoCargo(Integer codigo,
			String carCodigo, String descricao, DominioSituacao situacao,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc);

	
	public List<RapOcupacaoCargo> pesquisarOcupacaoCargo(String parametroPesquisa, DominioSituacao situacao);
	
	public List<RapOcupacaoCargo> pesquisarOcupacaoCargo(Integer codigo, String descricao, String carCodigo, DominioSituacao situacao);

	
	/**
	 * Retorna instância pelo id.
	 * 
	 * @param id
	 * @return
	 * @throws ApplicationBusinessException
	 */
	RapOcupacaoCargo obterOcupacaoCargo(RapOcupacoesCargoId id) throws ApplicationBusinessException;

	void alterar(RapOcupacaoCargo ocupacaoCargo);

	void remover(RapOcupacoesCargoId id) throws ApplicationBusinessException;

	Long pesquisarOcupacaoCargoCount(Integer codigo, String carCodigo, String descricao, DominioSituacao situacao);

	/**
	 * Retorna os cargos encontrados com a string fornecida no atributo
	 * descrição.
	 * 
	 * @param valor
	 * @return
	 */
	public List<OcupacaoCargoVO> pesquisarOcupacaoPorCodigo(
			String ocupacaoCargo, boolean somenteAtivos)
			throws ApplicationBusinessException;

	public RapGrupoFuncional pesquisarGrupoFuncionalPorCodigo(short codigo)
			throws ApplicationBusinessException;

	public Long montarConsultaCount(Short codigo, String descricao);

	public List<RapGrupoFuncional> pesquisarGrupoFuncional(Short codigo,
			String descricao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	public void editar(RapGrupoFuncional rapGrupoFuncional) throws ApplicationBusinessException;

	RapGrupoFuncional obterRapGrupoFuncional(Short codigo);
	
	void gravar(RapGrupoFuncional rapGrupoFuncional) throws ApplicationBusinessException;

	void excluir(Short codigoRapGrupoFuncional) throws ApplicationBusinessException;

	/**
	 * Retorna um lista de grupos funcionais encontrados com a string fornecida
	 * no atributo descrição.
	 * 
	 * @param valor
	 * @return
	 */
	public List<RapGrupoFuncional> pesquisarGrupoFuncional(String grupoFuncional);

	/**
	 * Pesquisar ramais telefonicos pelo numero do ramal
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public List<RapRamalTelefonico> pesquisarRamalTelefonicoPorNroRamal(
			Object paramPesquisa);

	void salvar(RapRamalTelefonico rapRamalTelefonico, boolean alteracao) throws ApplicationBusinessException;
	void excluirRamalTelefonico(Integer nrRamalTelefonico) throws ApplicationBusinessException;

	public List<RapRamalTelefonico> pesquisarRamalTelefonico(
			Integer numeroRamal, String indUrgencia, Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc);

	public RapRamalTelefonico obterRamalTelefonico(Integer id)
			throws ApplicationBusinessException;

	public Long pesquisarRamalTelefonicoCount(Integer numeroRamal,
			String indUrgencia);

	/**
	 * Método para salvar um novo vínculo
	 */
	public void salvar(RapVinculos vinculo) throws ApplicationBusinessException;

	/**
	 * Método para alterar um vínculo
	 */
	public void alterar(RapVinculos vinculo) throws ApplicationBusinessException;

	public void enviaEmail(Short codigo, String descricao,
			DominioSimNao geraMatricula) throws ApplicationBusinessException;

	/**
	 * Método para buscar um ConselhoProfissional na base de dados
	 */
	public RapConselhosProfissionais obterConselhoProfissional(
			Short codigoConselhoProfissional) throws ApplicationBusinessException;

	public void excluirConselhoProfissional(Short seq) throws ApplicationBusinessException;

	/**
	 * Método para pesquisar ConselhoProfissional na base de dados
	 */
	public List<RapConselhosProfissionais> pesquisarConselhosProfissionais(
			Short codigo, String nome, String sigla,
			DominioSituacao indSituacao, String tituloMasculino,
			String tituloFeminino, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, List<String>  conselhosLimitados);

	String obterSiglaConselho(Short codigo);
	
	/**
	 * Método para contar a quantidade de Conselho Profissionais pesquisados
	 */
	public Long pesquisarConselhosProfissionaisCount(Short codigo,
			String nome, String sigla, DominioSituacao indSituacao,
			String tituloMasculino, String tituloFeminino, List<String> conselhosLimitados);

	/**
	 * Método para alterar um ConselhoProfissional
	 */
	public void alterar(RapConselhosProfissionais conselhoProfissional)
			throws ApplicationBusinessException;

	/**
	 * Método para salvar um novo ConselhoProfissional
	 */
	public void salvar(RapConselhosProfissionais conselhoProfissional)
			throws ApplicationBusinessException;

	public Long pesquisarGraduacaoCount(Integer codigo, String descricao,
			DominioSituacao situacao, DominioTipoQualificacao tipo,
			RapConselhosProfissionais conselho);

	public List<RapTipoQualificacao> pesquisarGraduacao(Integer codigo,
			String descricao, DominioSituacao situacao,
			DominioTipoQualificacao tipo, RapConselhosProfissionais conselho,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	void removerGraduacao(Integer codigo) throws ApplicationBusinessException;

	/**
	 * Verificar se um novo registro de graduação deve ser inserido no banco.
	 * Quando o campo "tipo" for "CSC" e o nome do conselho tiver sido
	 * escolhido, a gravação
	 * 
	 * @param _rapTipoQualificacao
	 *            Objeto com os tipos de qualificação retornados na pesquisa.
	 * @return
	 */
	public void validarRegistroGraduacaoConselho(
			RapTipoQualificacao _rapTipoQualificacao)
			throws ApplicationBusinessException;

	public void alterar(RapTipoQualificacao rapTipoQualificacao)
			throws ApplicationBusinessException;

	public void salvar(RapTipoQualificacao rapTipoQualificacao)
			throws ApplicationBusinessException;

	RapTipoQualificacao obterGraduacao(Integer codGraduacao, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) throws ApplicationBusinessException;

	/**
	 * Retorna os conselhos ativos encontrados com a string fornecida no
	 * atributo descrição.
	 * 
	 * @param valor
	 * @return
	 */
	public List<RapConselhosProfissionais> pesquisarConselhosAtivosPorDescricao(
			String valor);

	/**
	 * Retorna os conselhos encontrados com a string fornecida no atributo
	 * descrição.
	 * 
	 * @param valor
	 * @return
	 */
	public List<RapConselhosProfissionais> pesquisarConselhosPorDescricao(
			String valor);

	/**
	 * Retorna os cargos encontrados com a string fornecida no atributo
	 * descrição.
	 * 
	 * @param valor
	 * @return
	 */
	public List<RapCargos> pesquisarCargosPorDescricao(String valor);

	public void salvar(RapTipoAfastamento rapTipoAfastamento, boolean alteracao)
			throws ApplicationBusinessException;

	public RapTipoAfastamento obterTipoAfastamento(String codigo)
			throws ApplicationBusinessException;

	public Long pesquisarTipoAfastamentoCount(String codigo,
			String descricao, DominioSituacao indSituacao);

	public List<RapTipoAfastamento> pesquisarTipoAfastamento(String codigo,
			String descricao, DominioSituacao indSituacao, Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc);

	void excluirTipoAfastamento(RapTipoAfastamento tipoAfastamento) throws ApplicationBusinessException;

	Long pesquisarInstituicaoQualificadoraCount(Integer codigo, String descricao, DominioSimNao indInterno, Boolean indUsoGppg);

	void excluirInstituicaoQualificadora(Integer codInstituicaoQualificadora) throws ApplicationBusinessException;

	RapInstituicaoQualificadora obterInstituicaoQualificadora(Integer id);

	List<RapInstituicaoQualificadora> pesquisarInstituicaoQualificadora(
			Integer codigo, String descricao, DominioSimNao indInterno,
			Boolean indUsoGppg, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc);

	void salvar(RapInstituicaoQualificadora rapInstituicaoQualificadora, boolean alteracao) throws ApplicationBusinessException;

	/**
	 * Retorna os cursos de graduação.
	 * 
	 * @param curso
	 * @return retorna apenas os 100 primeiros
	 */
	public List<RapTipoQualificacao> pesquisarCursoGraduacao(Object curso);

	/**
	 * Retorna instituições qualificadoras com os parâmetros fornecidos.
	 * 
	 * @param instituicaoObject
	 * @return retorna apenas os 100 primeiros
	 */
	public List<RapInstituicaoQualificadora> pesquisarInstituicao(
			Object instituicaoObject);

	public Long pesquisarCargoCount(String codigo, String descricao,
			DominioSituacao situacao);

	public List<RapCargos> pesquisarCargo(String codigo, String descricao,
			DominioSituacao situacao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	public RapCargos obterCargo(String codCargo) throws ApplicationBusinessException;

	public void removerCargo(String codGardo) throws ApplicationBusinessException;

	public void alterar(RapCargos rapCargos) throws ApplicationBusinessException;

	public void salvar(RapCargos rapCargos) throws ApplicationBusinessException;

	public RapTipoQualificacao obterTipoQualificacao(Integer chavePrimaria);

	public RapConselhosProfissionais obterConselhoProfissionalComNroRegConselho(
			Integer matricula, Short vinCodigo, DominioSituacao situacao);

	public RapTipoAtuacao obterTipoAtuacao(short codigo);

	public RapTipoAtuacao getTipoAtuacaoDiscente() throws ApplicationBusinessException;

	public String obterRapQualificaoComNroRegConselho(Integer matricula,
			Short vinCodigo, DominioSituacao situacao);

	public Long pesquisarConselhosAtivosPorDescricaoCount(String valor);
	
	public void incluirOcupacaoCargo(RapOcupacaoCargo ocupacaoCargo) throws ApplicationBusinessException;
	
	public List<RapConselhosProfissionais> listarConselhoProfissionalComNroRegConselho(
			Integer matricula, Short vinCodigo, DominioSituacao situacao);

	public RapTipoQualificacao obterQualificacaoEConselhoPorCodigo(Integer codigo);

}