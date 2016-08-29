package br.gov.mec.aghu.registrocolaborador.business;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import br.gov.mec.aghu.ambulatorio.vo.ProfissionalHospitalVO;
import br.gov.mec.aghu.ambulatorio.vo.VRapPessoaServidorVO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.controlepaciente.vo.DescricaoControlePacienteVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioAnamneseEvolucao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.dominio.DominioTipoRemuneracao;
import br.gov.mec.aghu.exames.vo.VRapServidorConselhoVO;
import br.gov.mec.aghu.faturamento.vo.VFatProfRespDcsVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.RapServidoresTransPacienteVO;
import br.gov.mec.aghu.internacao.vo.ServidorConselhoVO;
import br.gov.mec.aghu.internacao.vo.ServidoresCRMVO;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.CsePerfilProcessoLocal;
import br.gov.mec.aghu.model.CsePerfilProcessos;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.RapChefias;
import br.gov.mec.aghu.model.RapCodStarhLivres;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapDependentes;
import br.gov.mec.aghu.model.RapDependentesId;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoesId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapQualificacoesId;
import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.model.RarCandidatoPrograma;
import br.gov.mec.aghu.model.RarCandidatos;
import br.gov.mec.aghu.model.RarPrograma;
import br.gov.mec.aghu.model.RarProgramaDuracao;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.paciente.vo.SituacaoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.VMpmServConselhoGeralVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RapServidoresProcedimentoTerapeuticoVO;
import br.gov.mec.aghu.registrocolaborador.vo.AgendaProcedimentoPesquisaProfissionalVO;
import br.gov.mec.aghu.registrocolaborador.vo.CursorBuscaCboVO;
import br.gov.mec.aghu.registrocolaborador.vo.CursorCurPreVO;
import br.gov.mec.aghu.registrocolaborador.vo.OcupacaoCargoVO;
import br.gov.mec.aghu.registrocolaborador.vo.PessoaTipoInformacoesVO;
import br.gov.mec.aghu.registrocolaborador.vo.RapPessoalServidorVO;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.registrocolaborador.vo.VRapServCrmAelVO;
import br.gov.mec.aghu.vo.RapServidoresVO;

@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Local
public interface IRegistroColaboradorFacade extends Serializable {
	
	/**
	 * Retorna o servidor com o id fornecido.
	 * 
	 * @param id
	 * @return null se não encontrado
	 */
	
	RapServidores obterServidor(RapServidoresId id);

	/**
	 * Retorna um servidor ativo conforme o seu login de usuário do AGHU.
	 * Caso deseje usar as otimizações de cache, vide método:
	 * 
	 * @see obterServidorAtivoPorUsuario(final String login, final Date
	 *      dtFimVinculo)
	 * @param login
	 *            Usuário do servidor no sistema AGHU
	 * @return Servidor ativo até com data de fim do vínculo até agora
	 * @throws ApplicationBusinessException
	 */
	public RapServidores obterServidorAtivoPorUsuario(final String login)
		throws ApplicationBusinessException;
	
	/**
	 * Retorna um servidor ativo conforme o seu login de usuário do AGHU e data
	 * com data de fim de vínculo menor ou igual a informada por parâmetro
	 * OBSERVAÇÃO: Utilize este método, informando sempre a MESMA data de fim de
	 * vínculo por parâmetro, caso deseje fazer uso das otimizações da Cache
	 * 
	 * @param login
	 *            Usuário do servidor no sistema AGHU
	 * @param dtFimVinculo
	 *            Data em que expira o vínculo do servidor com a instituição
	 * @return Servidor ativo até a data de fim do vínculo passada para esta
	 *         pesquisa
	 * @throws ApplicationBusinessException
	 */
	RapServidores obterServidorAtivoPorUsuario(final String login, final Date dtFimVinculo) 
	throws ApplicationBusinessException;
	
	Long pesquisarServidoresAtivosPendentesCount(Object servidor);
	
	List<RapServidores> pesquisarServidoresAtivosPendentes(Object servidor);
	
	List<RapServidores> pesquisarServidoresAtivosPendentesMedicoProfessoreUnidade(Object servidor, short unfSeq);
	
	Long pesquisarServidoresAtivosPendentesMedicoProfessoreUnidadeCount(Object servidor, short unfSeq);
	
	/**
	 * Retorna um servidor conforme o seu login de usuário do AGHU
	 * 
	 * @param login Usuário do Servidor no sistema AGHU
	 * @return Servidor ativo ou inativo
	 */
	
	public RapServidores obterServidorPorUsuario(final String login) 
		throws ApplicationBusinessException;	

	RapServidores obterServidor(RapCodStarhLivres rapCodStarhLivres);

	RapPessoasFisicas obterPessoaFisica(Integer codigo)
			throws ApplicationBusinessException;
	

	RapPessoasFisicas obterPessoaFisicaCRUD(Integer codigo)
			throws ApplicationBusinessException;

	RapDependentes obterDependente(RapDependentesId id);

	RapDependentes obterDependente(RapDependentesId id, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);
	/**
	 * Retorna o primeiro servidor encontrado com os parâmetros fornecidos.
	 * 
	 * @param pesCodigo
	 *            - codigo da Pessoa
	 * 
	 * @return null se não encontrado
	 */
	RapServidores buscarServidorPesCodigo(Integer pesCodigo);

	void salvarDependente(RapDependentes dependentes)
			throws ApplicationBusinessException;

	Long pesquisarDependenteCount(Integer pesCodigo, Integer codigo,
			Integer serMatricula, Short serVinCodigo)
			throws ApplicationBusinessException;

	List<RapDependentes> pesquisarDependente(Integer pesCodigo, Integer codigo,
			Integer serMatricula, Short serVinCodigo, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc)
			throws ApplicationBusinessException;

	void alterar(RapDependentes dependente, RapServidores servidorLogado) throws ApplicationBusinessException;

	void alterar(RapServidores servidor, RapServidores servidorLogado) throws ApplicationBusinessException;

	void inserir(RapServidores servidor) throws ApplicationBusinessException;

	void removerDependente(RapDependentesId id)throws ApplicationBusinessException;
	
	String obterDescricaoOcupacaoTabelaSTARH(Integer codigoOcupacaoCargo) throws ApplicationBusinessException;
	
	List<RapPessoasFisicas> pesquisarPessoaFisica(String paramPesquisa);

	Long pesquisarPessoaFisicaCount(Object paramPesquisa);

	List<RapPessoasFisicas> pesquisarPessoaFisica(Integer codigo, String nome,
			Long cpf, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	/**
	 * Retorna pessoa física de acordo com o código ou nome informados
	 * 
	 * @dbtables RapPessoasFisicas select
	 * 
	 * @param codigo
	 *            ou nome
	 * @return pessoas físicas encontradas ou lista vazia se não encontrado
	 */
	List<RapPessoasFisicas> pesquisarPessoaFisicaPorCodigoNome(
			String pessoaFisica);

	Long pesquisarPessoaFisicaCount(Integer codigo, String nome, Long cpf);

	Integer obterNumeroDependentes(RapPessoasFisicas pessoaFisica);

	void desatachar(RapPessoasFisicas pessoaFisica);

	boolean existeVinculoDependentesPessoa(RapPessoasFisicas pf)
			throws ApplicationBusinessException;

	void alterar(RapPessoasFisicas pessoaFisica, RapServidores servidorLogado) throws ApplicationBusinessException;

	void salvar(RapPessoasFisicas pessoaFisica) throws ApplicationBusinessException;

	/**
	 * Retorna o primeiro servidor encontrado com os parâmetros fornecidos.
	 * 
	 * @param vinculo
	 * @param matricula
	 * @param nome
	 * 
	 * @return null se não encontrado
	 */
	RapServidores buscarServidor(Short vinculo, Integer matricula, String nome);

	String obterIdadeExtenso(Date dataParametro) throws ApplicationBusinessException;

	String obterTempoExtenso(Date dataInicioVinculo, Date dataFimVinculo)
			throws ApplicationBusinessException;

	/**
	 * Retornar infomação de afastamento
	 * 
	 * @param dataFimVinculo
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	String obterAfastamento(Date dataFimVinculo, Integer matricula,
			Short vinCodigo);

	RapServidores obterServidorAtivoPorUsuarioSemCache(final String loginUsuario) throws ApplicationBusinessException;
	
	/**
	 * Montar as confirmações que serão apresentadas na tela conforme regra de
	 * negócio
	 * 
	 * @param novo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	String montarConfirmacoes(RapServidores servidor, boolean edicao)
			throws ApplicationBusinessException;

	/**
	 * Retorna o servidor com os argumentos fornecidos.
	 * 
	 * @param vinculo
	 * @param matricula
	 * @return null se não encontrado
	 */
	
	RapServidores obterServidor(Short vinculo, Integer matricula);

	/**
	 * Retorna servidores de acordo com a matricula ou nome fornecido.
	 * 
	 * @dbtables RapServidores select
	 * 
	 * @param servidor
	 *            matricula ou nome
	 * @return servidores encontrados em ordem alfabética ou lista vazia se não
	 *         encontrado
	 */
	List<RapServidores> pesquisarServidor(Object servidor);

	List<RapServidores> pesquisarServidorPontoServidor(Object servidor);
	
	
	List<RapServidores> pesquisarServidorVinculoAtivoEProgramadoAtual(
			Object servidor);

	/**
	 * 
	 * @param pesCodigo
	 * @param codTipoInformacoes
	 * @return
	 */
	List<RapPessoaTipoInformacoes> listarPorPessoaFisicaTipoInformacao(
			Integer pesCodigo, Short[] codTipoInformacoes,
			final Date dtRealizado);

	/**
	 * 
	 * @param pesCodigo
	 * @param valorTipoInformacao
	 * @return
	 */
	String listarPorPessoaFisicaValorTipoInformacao(Integer pesCodigo,
			String[] valorTipoInformacao, final Date dtRealizado);

	RapServidores obterRapServidoresPorChavePrimaria(
			RapServidoresId chavePrimaria);

	void insert(RapCodStarhLivres rapCodStarhLivres)
			throws ApplicationBusinessException;

	/**
	 * Verifica se o tipo de informação passado é um CBO
	 */
	boolean isCbo(Short tiiSeq) throws ApplicationBusinessException;

	RapPessoaTipoInformacoes obterPessoaTipoInformacoes(
			RapPessoaTipoInformacoesId rapPessoaTipoInformacoesId)
			throws ApplicationBusinessException;
	String obterRapPessoaTipoInformacoesPorPesCPFTiiSeq(final Long cpf, final Short tiiSeq);
	RapPessoaTipoInformacoes obterPessoaTipoInformacoesSemRefresh(RapPessoaTipoInformacoesId rapPessoaTipoInformacoesId);
	List<PessoaTipoInformacoesVO> pesquisarPessoaTipoInformacoes(
			Integer pesCodigo, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	Long pesquisarPessoaTipoInformacoesCount(Integer pesCodigo);

	void salvar(RapPessoaTipoInformacoes pessoaTipoInformacoes, boolean alterar)
			throws ApplicationBusinessException;

	/**
	 * Retorna o servidor pelo id.
	 * 
	 * @param vinculo
	 * @param matricula
	 * @return
	 */
	RapServidores buscarServidor(Short vinculo, Integer matricula);

	void excluirPessoaTipoInformacao(RapPessoaTipoInformacoesId id)
			throws ApplicationBusinessException;

	/**
	 * Retorna a quantidade de registros de graduação encontrados com os
	 * parâmetros fornecidos.
	 * 
	 * @param pesCodigo
	 * @param serMatricula
	 * @param serVinCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	Long pesquisarGraduacaoCount(Integer pesCodigo, Integer serMatricula, Short serVinCodigo);

	/**
	 * Retorna as graduações com os parâmetros fornecidos.
	 */
	List<RapQualificacao> pesquisarGraduacao(Integer pesCodigo, Integer matricula, Short vinculo, Integer firstResult,Integer maxResult, String orderProperty, boolean asc);

	/**
	 * Altera a graduação do servidor.
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */
	void alterar(RapQualificacao qualificacao, RapServidores servidorLogado) throws ApplicationBusinessException;

	/**
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */
	
	void remover(RapQualificacao qualificacao) throws ApplicationBusinessException;

	/**
	 * Inclui graduação do servidor.
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */
	void incluir(RapQualificacao qualificacao) throws ApplicationBusinessException;

	RapQualificacao obterQualificacao(RapQualificacoesId id);
	
	RapQualificacao obterQualificacao(RapQualificacoesId id, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	Long pesquisarQualificacoesCount(Integer codigoPessoa,
			String nomePessoa, String numRegistro, String siglaConselho);

	List<RapQualificacao> pesquisarQualificacoes(Integer codigoPessoa,
			String nomePessoa, String numRegistro, String siglaConselho,
			Integer firstResult, Integer maxResults);

	Long pesquisarServidoresCount(Integer codigoCCLotacao,
			Integer codVinculo, Integer matricula, String nomeServidor,
			Integer codigoCCAtuacao) throws ApplicationBusinessException;

	FccCentroCustos obterCentroCusto(FccCentroCustos codigo) throws ApplicationBusinessException;

	void salvar(RapServidores rapServidor) throws ApplicationBusinessException;

	List<FccCentroCustos> pesquisarCentroCustosAtivosOrdemDescricao(
			Object centroCusto);

	List<RapServidores> pesquisarServidores(Integer codigoCCLotacao,
			Integer codVinculo, Integer matricula, String nomeServidor,
			Integer codigoCCAtuacao, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) throws ApplicationBusinessException;

	List<FccCentroCustos> pesquisarCentroCustosOrdemDescricao(Object centroCusto);

	/**
	 * ORADB: Function RAPC_BUSCA_COD_STARH
	 * 
	 * Versão java da Function RAPC_BUSCA_COD_STARH
	 * 
	 * @param
	 * @return codStarh
	 * @throws ApplicationBusinessException
	 * @see {@link br.gov.mec.aghu.dao.ObjetosOracleDAO}
	 */
	Integer obterProximoCodStarhLivre();

	/**
	 * Pesquisa todos os servidores a partir de um nome, ou parte dele, e por
	 * uma lista de usuários Este método foi feito para reproduzir, em parte, a
	 * view V_FAT_MEDICOS_AUDITORES
	 * 
	 * @param nome
	 *            Nome, ou parte, de uma servidor
	 * @param listaUsuarios
	 *            Lista contendo logins de usuários do sistema
	 * @param ativo
	 *            Indicador que informa se deve retornar apenas os ativos
	 *            (true), ou todos (false)
	 * @return Servidores que contemplem os filtros de pesquisa ou lista vazia
	 *         caso contrário
	 */
	List<RapServidores> pesquisarServidorPorNomeEUsuarios(String nome,
			Collection<String> listaUsuarios, Boolean ativo);
	
	List<RapServidores> pesquisarServidoresPorTipoInformacao(final String filtro, final DominioSituacaoVinculo situacao, final String tipoInformacao);
	
	Long pesquisarServidoresPorTipoInformacaoCount(final String filtro, final DominioSituacaoVinculo situacao, final String tipoInformacao);

	Long pesquisarServidorPorNomeEUsuariosCount(String nome,
			Collection<String> listaUsuarios, Boolean ativo);

	
	String obterPrimeiroNroRegistroConselho(Integer matricula, Short vinculo);

	String buscarNroRegistroConselho(Short vinCodigo, Integer matricula);

	
	String obterNomeServidor(Integer matricula, Short vinculo);

	Long pesquisarCandidatosPorNacionalidadeCount(
			AipNacionalidades nacionalidade);

	/**
	 * Retorna rapServidores de acordo com o codigo passado por parametro
	 * 
	 * @param codigo
	 * @return
	 */
	RapServidores pesquisarResponsavel(Short codigo, Integer matricula,
			String nomeResponsavel);

	/**
	 * Retorna rapServidores de acordo com a matricula ou nome passado por
	 * parametro
	 * 
	 * @dbtables RapServidores select
	 * 
	 * @param responsavel
	 * @return
	 */
	
	List<RapServidores> pesquisarResponsaveis(Object responsavel);

	
	List<ServidorConselhoVO> pesquisarServidorConselhoVOPorNomeeCRM(
			Object strPesquisa);

	List<ServidoresCRMVO> pesquisarServidorCRMVOPorNomeeCRM(Object strPesquisa);

	RapServidores pesquisaRapServidorPorSituacaoPacienteVO(
			SituacaoPacienteVO situacaoPacienteVO);

	
	List<EspCrmVO> pesquisaProfissionalEspecialidade(
			AghEspecialidades especialidade, String strPesquisa)
			throws ApplicationBusinessException;

	
	List<RapServidores> pesquisarServidores(String paramPesquisa,
			AghEspecialidades especialidade, FatConvenioSaude convenio,
			Integer[] tipoQualificacao, Integer maxResults)
			throws ApplicationBusinessException;

	List<RapServidoresTransPacienteVO> pesquisarServidoresPorNomeOuCRM(
			String paramPesquisa, AghEspecialidades especialidade,
			FatConvenioSaude convenio, Integer[] tipoQualificacao,
			Integer maxResults) throws ApplicationBusinessException;

	/**
	 * Retorna o número de registro no conselho profissional.
	 * 
	 * @param pessoa
	 *            id da pessoa física
	 * @return
	 * @throws ApplicationBusinessException
	 */
	String buscaNumeroRegistro(Integer pessoa, Integer[] tiposQualificacao)
			throws ApplicationBusinessException;

	
	RapServidores obterSubstituto(Short vinculoSubstituto,
			Integer matriculaSubstituto);

	/**
	 * Procura profissional substituto para os parâmetros fornecidos.<br>
	 * Substitutos são os profissionais que tem escala para a especialidade e
	 * convênio no dia fornecido.
	 * 
	 * @param especialidadeId
	 *            id da especialidade
	 * @param convenioId
	 *            id do convênio
	 * @param data
	 * @return matriculas(RapServidores) dos profissionais substitutos
	 * @throws ApplicationBusinessException
	 */
	List<RapServidores> pesquisarProfissionaisSubstitutos(
			Short especialidadeId, Short convenioId, Date data,
			Object substitutoPesquisaLOV, Integer medicinaId,
			Integer odontologiaId) throws ApplicationBusinessException;

	
	List<RapServidores> pesquisarRapServidoresPorCodigoDescricao(
			Object objPesquisa);

	/**
	 * @param objPesquisa
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#pesquisarRapServidoresVOPorCodigoDescricao(java.lang.Object)
	 */
	
	List<RapServidoresVO> pesquisarRapServidoresVOPorCodigoDescricao(
			Object objPesquisa);

	/**
	 * @param id
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#obterRapServidor(br.gov.mec.aghu.model.RapServidoresId)
	 */
	
	RapServidores obterRapServidor(RapServidoresId id);

	/**
	 * @param id
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#obterRapPessoasFisicas(br.gov.mec.aghu.model.RapServidoresId)
	 */
	RapPessoasFisicas obterRapPessoasFisicas(RapServidoresId id);

	/**
	 * @param codigoPessoa
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#pesquisarRapServidoresPorCodigoPessoa(java.lang.Integer)
	 */
	
	List<RapServidores> pesquisarRapServidoresPorCodigoPessoa(
			Integer codigoPessoa);

	/**
	 * @param matricula
	 * @param vinculo
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#buscaConselhosProfissionalServidor(java.lang.Integer,
	 *      java.lang.Short)
	 */
	List<ConselhoProfissionalServidorVO> buscaConselhosProfissionalServidor(
			Integer matricula, Short vinculo);

	List<ConselhoProfissionalServidorVO> buscaConselhosProfissionalServidorAtivoInativo(
			Integer matricula, Short vinculo);	
	
	/**
	 * @param param
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#pesquisarRapServidores(java.lang.Object)
	 */
	
	List<RapServidores> pesquisarRapServidores(Object param);

	/**
	 * @param param
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#pesquisarRapServidoresCount(java.lang.Object)
	 */
	Long pesquisarRapServidoresCount(Object param);

	/**
	 * @param servidor
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#obterServidor(br.gov.mec.aghu.model.RapServidores)
	 */
	
	RapServidores obterServidor(RapServidores servidor);

	/**
	 * @param chefia
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#incluirChefia(br.gov.mec.aghu.model.RapChefias)
	 */
	
	void incluirChefia(RapChefias chefia) throws ApplicationBusinessException;

	/**
	 * Chama validações para averiguar se o vínculo do servidor ainda é válido.
	 * ATENÇÃO: NÃO colocar restrição de segurança neste método pois ele é
	 * chamado no momento de login, quando o contexto de segurança ainda não
	 * está pronto.
	 * 
	 * @param servidor
	 *            Servidor a ser verificado
	 * @throws ApplicationBusinessException
	 *             Em caso de alguma validação de negócio falhar
	 */
	void verificarVinculoServidor(RapServidores servidor)
			throws ApplicationBusinessException;

	/**
	 * Chama validações para averiguar se o vínculo do servidor ainda é válido.
	 * ATENÇÃO: NÃO colocar restrição de segurança neste método pois ele é
	 * chamado no momento de login, quando o contexto de segurança ainda não
	 * está pronto.
	 * 
	 * @param login
	 *            Usuário do AGHU a ser verificado
	 * @throws ApplicationBusinessException
	 *             Em caso de alguma validação de negócio falhar
	 * @throws ApplicationBusinessException
	 */
	void verificarVinculoServidor(String login) throws ApplicationBusinessException,
			ApplicationBusinessException;

	List<RapPessoasFisicas> suggestionPessoasFisicasPorCPFNome(
			final Object objPesquisa);

	Long suggestionPessoasFisicasPorCPFNomeCount(final Object objPesquisa);

	List<RapServidores> pesquisarServidorAtivoPorVinculos(Object paramPesquisa,
			List<Short> vinculos);

	RapServidores buscaServidor(final RapServidoresId servidorId);

	List<RapServidores> pesquisarServidorCertificadoDigital(
			final Object paramPesquisa, final FccCentroCustos centroCusto);

	Long pesquisarServidorCount(RapRamalTelefonico ramalTelefonico);

	List<RapServidores> listarServidoresComPessoaFisicaPorNome(String parametro);

	String obterNomePessoaServidor(Integer cod, Integer matricula);
	
	String obterNomePessoaServidor(Short vinCodigo, Integer matricula);
	
	/**
	 * Efetua as pesquisas de servidores ativos/ programados por Vínculo e matrícula; descrição do vínculo ou nome
	 * 
	 * @param pesquisa Vínculo e matrícula; descrição do vínculo ou nome
	 * @return List<RapServidores>
	 */
	List<RapServidores> pesquisarServidoresPorVinculoMatriculaDescVinculoNome(Object objetoPesquisa);
	
	Integer pesquisarServidoresPorVinculoMatriculaDescVinculoNomeCount(Object objetoPesquisa);

	List<RapServidores> pesquisarServidoresPorCodigoOuDescricao(Object objetoPesquisa);
	
	Long pesquisarServidoresPorCodigoOuDescricaoCount(Object objetoPesquisa);
	
	RapServidores obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(Integer matricula, Short vinCodigo);
	
	RapServidores obterServidorAtivoPorCpf(Long cpf);
	
	List<RapServidores> pesquisarServidorSuggestion(Object servidor, String emaExaSigla, Integer emaManSeq);

	List<RapServidoresVO> pesquisarServidor(final Object strPesq,
			final Short tipoCBO) throws BaseException;

	Long pesquisarServidorCount(Object strPesq, final Short tipoCBO)
			throws BaseException;
	
	List<RapServidoresVO> pesquisarServidorPorCbo(final Object servidor, final Short tipoCBO, final List<String> cbos);
	
	Long pesquisarServidorPorCboCount(final Object servidor, final Short tipoCBO, final List<String> cbos);

	RapServidoresVO obterServidorVO(final Integer pesCodigo,
			final Integer matricula, final Short vinCodigo, final Short tipoCBO)
			throws BaseException;
	
	List<RapServidores> pesquisarServidorPorSituacaoAtivo(Object strPesq)
			throws BaseException;
	
	public Long pesquisarServidorPorSituacaoAtivoCount(Object strPesq) throws BaseException;

	List<RapServidores> pesquisarServidores(Object strPesq);

	Long pesquisarServidoresCount(Object strPesq);

	RapServidores obterServidorPeloProntuarioPeloVinculoEMatricula(
			Integer prontuario, Short vinCodigo, Integer matricula);
	
	/**
	 * @dbtables RapServidores select
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param matricula
	 * @param vinculo
	 * @param nome
	 * @return
	 */
	List<RapServidores> pesquisarRapServidores(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer matricula, Integer vinculo, String nome);
	
	/**
	 * @dbtables RapServidores select
	 * @param matricula
	 * @param vinculo
	 * @param nome
	 * @return
	 */
	Long pesquisarRapServidoresCount(Integer matricula, Integer vinculo, String nome);
	
	RapServidores atualizarRapServidores(RapServidores rapServidor);
	
	List<String> pesquisarServidorAtivo(Object paramPesquisa);
	
	/**
	 * obj[0] - rap_pessoas_fisicas.nome 
	 * obj[1] - rap_pessoas_fisicas.sexo
	 * obj[2] - rap_vinculos.titulo_masculino 
	 * obj[3] - rap_vinculos.titulo_feminino 
	 * obj[4] - rap_conselhos_profissionais.titulo_masculino 
	 * obj[5] - rap_conselhos_profissionais.titulo_feminino
	 * 
	 * @param vinCodigo
	 * @param matricula
	 * @return
	 */
	Object[] obtemDadosServidor(Short vinCodigo, Integer matricula);
	
	/**
	 * Responsavel por buscar uma lista de RapServidores que contenham <b>nomeServidor</b> como parte do nome.<br>
	 * Busca migrada conforme view:<br>
	 * 
	 * ORADB view AGH.V_RAP_SERV_SOL_EXME.<br> 
	 * 
	 * Joins foram adaptados devido ao nao utilizacao das tabelas CSE_* que forma trocadas pra utilizacao de CSC_*.<br>
	 * 
	 * <b>Todas a regras do AGH atualmente foram migradas.</b><br>
	 * 
	 * <b>Melhoria Solicitada:</b>
	 * Regras pra Conselho e Qualificacao. Espera por definicao com Arquitetura.<br>
	 * 
	 * @param nomeServidor
	 * @param diasServidorFimVinculoPermitidoSolicitarExame default 365 dias.
	 * @return
	 */
	List<RapServidores> pesquisarServidoresSolicitacaoExame(String nomeServidor, Integer diasServidorFimVinculoPermitidoSolicitarExame);
	
	List<RapServidores> pesquisarServidoresSolicitacaoExame(Short vinculo, Integer matricula, Integer diasServidorFimVinculoPermitidoSolicitarExame);
	
	List<RapQualificacao> pesquisarQualificacoesSolicitacaoExameSemPermissao(Short vinculo, Integer matricula, Integer diasServidorFimVinculoPermitidoSolicitarExame);
	
	/**
	 * Obtém RapServidor por Vínculo ou Matrícula
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	RapServidores obterRapServidorPorVinculoMatricula(Integer matricula,  Short vinCodigo);
	
	
	List<RapServidores> pesquisarServidorLiberaExames(final Object paramPesquisa);
	
	RapServidores pesquisarServidorLiberaExames(final Integer matricula,final Short vinCodigo);
	
	RapServidores buscaVRapServSolExme(final Short vinculo,
			final Integer matricula,
			final Integer diasServidorFimVinculoPermitidoSolicitarExame,
			final String numeroConselho, final String siglaConselho)
			throws ApplicationBusinessException;
	
	List<RapQualificacao> pesquisarQualificacoes(RapPessoasFisicas pessoaFisica);
	
	RapQualificacao obterRapQualificacaoPorServidor(RapServidores servidor);
	
	List<RapQualificacao> pesquisarQualificacaoConselhoProfissionalPorServidor(Integer serMatricula, Short serVinCodigo);
	
	List<RapConselhosProfissionais> listarRapConselhosProfissionaisPorPessoaFisica(RapPessoasFisicas pessoaFisica);
	
	List<RapServidoresVO> listarRapConselhosProfissionaisPorServidor(
			List<RapServidoresId> servidores);
	
	List<RapServidores> pesquisarServidoresCompradorAtivo(Integer matricula,
			Short vinCodigo, Integer codigoOcupacao);
	
	String obterNomePessoaServidorPorAelExameApItemSolics(Long luxSeq);
	
	boolean isServidorCompradorAtivoPorVinculoMatricula(Short vinculo, Integer matricula);
	
	RapDependentes obterDependentePeloPacCodigoPeloVinculoEMatricula(
			Integer codigo, Short vinCodigo, Integer matricula);
	
	List<RapDependentes> listarDependentesPorCodigoPaciente(Integer pacCodigo);
	
	RapDependentes atualizarRapDependentes(RapDependentes rapDependentes);
	
	List<RapPessoaTipoInformacoes> listarRapPessoaTipoInformacoesPorPesCodigoTiiSeq(final Integer pesCodigo, final Short tiiSeq);
	
	/**
	 * ORADB: FATK_CAP_UNI.RN_CAPC_CBO_PROC_RES.c_get_valor_cbo
	 * 
	 * @param pesCodigo
	 * @param tipoInf
	 * @param tipoInfSec
	 * @return
	 */
	List<CursorBuscaCboVO> listarPessoaTipoInformacoes(final Integer pesCodigo, final Date dtRealizado, List<Short> resultSeqTipoInformacaoShort, Date ultimoDiaMes);
	
	boolean existePessoaFisicaComTipoInformacao(Integer pesCodigo, Short[] codTipoInformacoes);
	
	/**
	 * @deprecated usar {@link #listarPessoasFisicasPorCodigoPaciente(Integer)}
	 * @param pacCodigo
	 * @return
	 */
	List<RapPessoasFisicas> obterPessoaFisicaPorCodigoPaciente(Integer pacCodigo);
	
	List<RapPessoasFisicas> listarPessoasFisicasPorCodigoPaciente(Integer pacCodigo);
	
	void persistirRapPessoasFisicas(RapPessoasFisicas rapPessoasFisicas);
	
	Long pesquisarPessoasFisicasPorNacionalidadeCount(AipNacionalidades nacionalidade);
	
	List<VFatProfRespDcsVO> pesquisarVFatProfRespDcsVO(final String[] sigla, final Object filtros, final boolean isLoad);

	Long pesquisarVFatProfRespDcsVOCount(final String[] sigla, final Object filtros);
	
	/**
	 * @param param
	 * @param unfSeq
	 * @param seqp
	 * @return listaRapServidoresVO
	 */
	List<RapServidoresVO> pesquisarRapServidoresPorUnidFuncESala(Object param, Short unfSeq, Short seqp);
	
	List<RapServidores> pesquisarServidoresVinculados(Object servidor) throws BaseException;
	
	VRapServidorConselho obterVRapServidorConselhoPeloId(int matricula,
			short vinCodigo, String sigla);
	
	Long pesquisarProfissionaisAgendaProcedimentosEletUrgOuEmerCount(final Object strPesquisa, final Short unfSeq); 
	
	List<VRapServidorConselho> pesquisarProfissionaisAgendaProcedimentosEletUrgOuEmer(final Object objPesquisa, final Short unfSeq);
	
	Long obterVRapServidorConselhoExamePeloId(int matricula, short vinCodigo, String emaExaSigla, Integer emaExaManSeq);

	List<VRapServidorConselhoVO> obterVRapServidorConselhoVO(Long luxSeq);

	Boolean validarPermissaoAmbulatorioItensPorServidor(String login,
			DominioAnamneseEvolucao dominio);
	
	List<CsePerfilProcessoLocal> pesquisarPerfisProcessoLocal(DominioSimNao indConsulta, DominioSituacao indSituacao, Short paramSeqProcConsPol, Integer codigoCentroCustoLotacao);
	
	List<CsePerfilProcessos> pesquisarPerfisProcessos(Boolean indConsulta, DominioSituacao indSituacao, Short paramSeqProcConsPol);

	RarCandidatoPrograma obterRarCandidatoProgramaPorServidorData(RapServidores rapServidor, Date data);
	
	RarCandidatoPrograma obterRarCandidatoProgramaPorCandidatoData(RarCandidatos candidatos, Date data);
	
	RarCandidatoPrograma obterRarCandidatoProgramaPorProgramaCandidato(RarCandidatos candidato, RarPrograma programa);
	
	RarCandidatoPrograma obterUltimoRarCandidatoProgramaPorCandidato(RarCandidatos candidatos);

	RarCandidatos obterRarCandidatoPorChavePrimaria(Integer cndSeq);

	RarProgramaDuracao obterRarProgramaDuracaoPorRarProgramaAtualDataInicio(RarPrograma vPgaAtual, Date adicionaDias, Boolean false1);

	ConselhoProfissionalServidorVO buscaConselhoProfissional(Integer matricula,	Short vinCodigo);
	
	List<VRapServidorConselho> pesquisarConselhoPorMatriculaVinculo(final Integer matricula, final Short vinculo);

	public Integer pesquisarServidoresSolicitacaoExameCount(String nomeServidor, Integer diasServidorFimVinculoPermitidoSolicitarExame);

	List<RapServidores> pesquisarServidorPorVinculo(Object servidor);

	List<RapServidores> pesquisarServidorPorMatriculaNome(Object servidor);

	Long pesquisarServidorPorVinculoCount(Object servidor);

	Long pesquisarServidorPorMatriculaNomeCount(Object servidor);

	List<ConselhoProfissionalServidorVO> buscaConselhosProfissionalServidor(
			Integer matricula, Short vinculo, Boolean testaDataFimVinculo);
	
	String buscarNroRegistroConselho(Short vinCodigo, Integer matricula, boolean verificaDataFimVinculo);

	public List<VRapServCrmAelVO> pesquisarViewRapServCrmAelVO(VRapServCrmAelVO filtro, String[] parametros);
	
	public DescricaoControlePacienteVO buscarDescricaoAnotacaoControlePaciente(Short vinCodigo, Integer matricula);

	Boolean existeQualificacoesUsuarioSemNumeroRegistroConselho(
			DominioTipoQualificacao tipoQualificacao);	
	
	List<RapServidores> pesquisarResidente(Object obj, Short vincFunc, Integer matriculaResidente);
	
	Long pesquisarResidenteCount(Object obj, Short vincFunc, Integer matriculaResidente);

	List<ConselhoProfissionalServidorVO> buscaConselhosProfissionalServidorRegConselhoNotNull(
		Integer matricula, Short vinCodigo, DominioSituacao dominioSituacao);
	
	List<RapServidores> pesquisarRapServidores();
	
	Long pesquisarRapServidoresCount();
	
	List<RapServidores> pesquisarServidoresCompradorAtivoPorMatriculaNome(Object objPesquisa);	
	
	List<RapServidores> pesquisarServidoresCompradorPorMatriculaNome(Object objPesquisa);

	List<RapServidores> pesquisarCompradoresAtivos(Object parametro, Integer first, Integer max, String order, Boolean asc);
	
	List<ProfDescricaoCirurgicaVO> pesquisarServidorConselhoNroRegNaoNuloListaSigla(List<String> listaSigla, Object objPesquisa);
	
	List<ProfDescricaoCirurgicaVO> pesquisarServidorConselhoNroRegNuloListaSigla(List<String> listaSigla, Object objPesquisa);
	
	List<ProfDescricaoCirurgicaVO> pesquisarServidorConselhoListaSiglaPorServidor(List<String> listaSigla, Integer serMatricula, Short serVinCodigo);
	
	String buscarNomeEquipePorPucSerMatricula(Integer serMatricula);
	
	List<RapServidores> obterServidorPorCPF(Long cpf);

	String buscaCbo(Integer matricula, Short vinCodigo, Short[] codTipoInformacoes);
	
	List<RapServidores> pesquisarServidorPorSituacaoAtivoParaProcedimentos(Object servidor, Short unfSeq) throws BaseException;
	Integer pesquisarServidorPorSituacaoAtivoParaProcedimentosCount(Object servidor, Short unfSeq) throws BaseException;

	RapServidores buscarRapServidorDeAelExtratoItemSolicitacao(Integer iseSoeSeq, Short iseSeqp, String codSit);
	
	List<RapServidores> pesquisarServidoresExameView(Short vinculo,
			Integer matricula,
			Integer diasServidorFimVinculoPermitidoSolicitarExame);
	
	Long buscaNroFuncionariosPorCentroCusto(FccCentroCustos centroCustos);

	Long buscaNroLoginsPorCentroCusto(FccCentroCustos centroCustos);
	
	Long buscaNroDependentesAbaixo7AnosPorCentroCusto(FccCentroCustos centroCusto);
	
	
	Long pesquisarResponsaveisCount(Object responsavel);

	List<RapPessoasFisicas> listarRapPessoasFisicasPorConselhoProfissional(
			String strPesquisa, List<String> listaConselhoProfissional);

	Long listarRapPessoasFisicasPorConselhoProfCount(String strPesquisa, List<String> listaConselhoProfissional);
	
	String obterNomeEquipeProfissionalMonitorCirurgia(MbcProfCirurgias profCirurgias);

	Long pesquisarServidorCount(Short vinCodigo, Integer matricula,
			DominioSituacaoVinculo indSituacao, RapPessoasFisicas pessoaFisica,
			String usuario, FccCentroCustos codigoCCustoLotacao,
			FccCentroCustos codigoCCustoAtuacao,
			DominioTipoRemuneracao tipoRemuneracao, OcupacaoCargoVO ocupacao, Date dataAtual, Date proximaData);

	List<RapServidores> pesquisarServidor(Short vinCodigo, Integer matricula,
			DominioSituacaoVinculo indSituacao, RapPessoasFisicas pessoaFisica,
			String usuario, FccCentroCustos centroCustoLotacao,
			FccCentroCustos centroCustoAtuacao,
			DominioTipoRemuneracao tipoRemuneracao,
			OcupacaoCargoVO ocupacaoCargo, Integer firstResult,
			Integer maxResult, String string, boolean b, Date dataAtual, Date proximaData);
			
	
	List<AgendaProcedimentoPesquisaProfissionalVO> pesquisarProfissionaisAgendaProcedimentoENotaConsumo(String strPesquisa, final Short unfSeq, List<String> listaConselhos, boolean agenda);
	Long pesquisarProfissionaisAgendaProcedimentoENotaConsumoCount(String strPesquisa, Short unfSeq, List<String> listaConselhos, boolean agenda);
	

	Long pesquisarProfissionaisAgendaProcedimentoCount(String strPesquisa, final Short unfSeq);
		
	boolean isServidorVinculadoCentroCusto(Short vinculo, Integer matricula, Integer cctCodAplicada);
	

	String obterChefeComprasPorMatriculaVinculo(int intValue, short vinculo);
	
	RapPessoasFisicas obterChefeServicoComprasPorAghParametros();
	
	RapPessoasFisicas obterRapPessoasFisicasPorServidor(RapServidoresId id);
	
	List<RapServidores> listarUsuariosNotificaveis(Integer codigoCentroCusto);
	
	List<AgendaProcedimentoPesquisaProfissionalVO> pesquisarProfissionaisRegistroCirurgiaRealizada(Object strPesquisa, final Short unfSeq);

	Long pesquisarProfissionaisRegistroCirurgiaRealizadaCount(Object strPesquisa, final Short unfSeq);

	RapServidores obterServidorPessoa(RapServidores servidor);

	/**
	 * Retorna dados de servidores ativos por nome, vínculo e matrícula
	 * 
	 * Web Service #34399
	 * 
	 * @param vinCodigo
	 * @param matricula
	 * @param nome
	 * @param maxResults
	 * @return
	 */
	List<RapServidores> pesquisarServidoresAtivos(Short vinCodigo, Integer matricula, String nome, Integer maxResults);
	
	/**
	 * Retorna count de servidores ativos por nome, vínculo e matrícula
	 * 
	 * Web Service #34399
	 * 
	 * @param vinCodigo
	 * @param matricula
	 * @param nome
	 * @return
	 */
	Long pesquisarServidoresAtivosCount(Short vinCodigo, Integer matricula, String nome);

	/**
	 * Retorna dados de médicos que atuam na emergência pelo nome, matrícula e código do vínculo
	 * 
	 * Web Service #34397
	 * 
	 * @param vinCodigos
	 * @param matriculas
	 * @param nome
	 * @return
	 */
	List<RapServidores> pesquisarMedicosEmergencia(List<Short> vinCodigos, List<Integer> matriculas, String nome);
	
	/**
	 * Retorna dados de médicos que atuam na emergência pelo nome, matrícula e código do vínculo
	 * 
	 * Web Service #34397
	 * 
	 * @param vinCodigos
	 * @param matriculas
	 * @param nome
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	List<RapServidores> pesquisarMedicosEmergencia(List<Short> vinCodigos, List<Integer> matriculas, String nome,
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc);
	
	/**
	 * Retorna count de médicos que atuam na emergência pelo nome, matrícula e código do vínculo
	 * 
	 * Web Service #34397
	 * 
	 * @param vinCodigos
	 * @param matriculas
	 * @param nome
	 * @return
	 */
	Long pesquisarMedicosEmergenciaCount(List<Short> vinCodigos, List<Integer> matriculas, String nome);

	RapPessoasFisicas obterRapPessoasFisicas(Integer codigo);
	
	/**
	 * #36698 - Pesquisa profissional pela central de custo
	 * @param strPesquisa
	 * @param centroCusto
	 * @return
	 */
	List<VRapServidorConselho> pesquisarServidoresConselho(String strPesquisa, List<Integer> centroCusto);
	
	/**
	 * #36698 - Count Pesquisa profissional pela central de custo
	 * @param strPesquisa
	 * @return
	 */
	Long pesquisarServidoresConselhoCount(String strPesquisa,List<Integer> centroCusto);

	/**
	 * Buscar profissional por centro de custo
	 * 
	 * Web Service #38778
	 * 
	 * @param siglas
	 * @param centroCusto
	 * @param maxResults
	 * @return
	 */
	List<VRapServidorConselho> pesquisarServidoresConselhoPorSiglaCentroCusto(final List<String> siglas, final List<Integer> centroCusto, Integer maxResults);

	/**
	 * Count de profissional por centro de custo
	 * 
	 * Web Service #38778
	 * 
	 * @param siglas
	 * @param centroCusto
	 * @return
	 */
	Long pesquisarServidoresConselhoPorSiglaCentroCustoCount(final List<String> siglas, final List<Integer> centroCusto);

	/**
	 * Buscar profissional por matricula e vínculo
	 * 
	 * Web Service #38729
	 * 
	 * @param siglas
	 * @param matricula
	 * @param vinculo
	 * @return
	 */
	VRapServidorConselho obterServidorConselhoPorSiglaMatriculaVinculo(final List<String> siglas, final Integer matricula, final Short vinculo);
	
	/**
	 * #36699 - Serviço para obter pessoa fisica
	 * @param vinculos
	 * @param matriculas
	 * @return
	 */
	List<RapPessoasFisicas> obterRapPessoasFisicasPorMatriculaVinculo(final List<Short> vinculos, final List<Integer> matriculas);
	
	String buscarNomeResponsavelPorMatricula(Short codigo, Integer matricula);

	boolean verificarServidorHUCadastradoPorCpf(Long numCpf);

	boolean verificarServidorHUCadastradoPorRg(String numRg);
	
	boolean usuarioTemPermissaoParaImprimirRelatoriosDefinitivos(Integer matricula, Short vinCodigo, String[] siglas);
	
	public List<RapServidores> pesquisarServidoresOrdenadoPorVinCodigo(Object strPesq);
	
	
	/**
	 * Obtem Map com o conselho servidor através dos atributos da ID atraves de listas.
	 * 
	 * map key: matricula-vinculo-sigla
	 * 
	 * @return
	 */
	Map<String, String>  obterMapRegistroVRapServidorConselho(List<Integer> listMatricula, List<Short> listVinCodigo, List<String> listSigla);

	
	
	/**
	 * Retorna true se o servidor estiver com vinculo ativo na data fornecida.
	 * 
	 * @param servidor
	 * @param aData
	 * @return
	 */
	public boolean isAtivo(RapServidores servidor, java.util.Date aData);

	/**
	 * Retorna true se o servidor estiver com vinculo ativo na data corrente.
	 * 
	 * @param servidor
	 * @return
	 */
	public boolean isAtivo(RapServidores servidor);
	

	Long pesquisarServidorPorVinculoMatriculaNomeCount(Object servidor);

	List<RapServidores> pesquisarServidorPorVinculoMatriculaNome(Object servidor);

	
	/**
	 * #8990 - Lista vinculos de funcionario por codigo e descricao
	 * 
	 * @param orderProperty
	 * @param asc
	 * @param filtro
	 * @return List<RapVinculos>
	 */
	public List<RapVinculos> pesquisarVinculoFuncionario(Object filtro);

	/**
	 * #8990 - Count da pesquisarViculoFuncionario
	 * 
	 * @param filtro
	 * @return Long
	 */
	public Long pesquisarVinculoFuncionarioCount(Object filtro);

	/**
	 * #8990 - Lista cargos de funcionario por descricao do cargo, codigo do
	 * cargo, ocupacao e situacao
	 * 
	 * @param orderProperty
	 * @param asc
	 * @param filtro
	 * @return List<RapOcupacaoCargo>
	 */
	public List<RapOcupacaoCargo> pesquisarOcupacaoCargoFuncionario(Object filtro);

	/**
	 * #8990 - Count do pesquisarOcupacaoCargoFuncionario
	 * 
	 * @param filtro
	 * @return Long
	 */
	public Long pesquisarOcupacaoCargoFuncionarioCount(Object filtro);

	/**
	 * #8990 - Avalia se Hospital é HCPA 
	 * 
	 */
	public boolean isHospitalHCPA();
	
	
	List<RapServidoresVO> pesquisarFuncionarios(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			RapServidores filtro);
	
	Long pesquisarFuncionariosCount(RapServidores filtro);
	
	/**
	 * Obtém os dados da view V_MPM_SERV_CONSELHO_GERAL, usando como filtro o ID do Servidor
	 * 
	 * @param matricula - Matrícula do Servidor
	 * @param vinCodigo - Código de vínculo do Servidor
	 * 
	 * @return Dados do medico solicitante
	 */
	public VMpmServConselhoGeralVO obterServidorConselhoGeralPorIdServidor(Integer matricula, Short vinCodigo);

	public String obterRegistroVRapServidorConselhoPeloId(int matricula,	short vinCodigo, String sigla);

	Long pesquisarServidorConselhoNroRegNuloListaSiglaCount(
			List<String> listaSigla, Object objPesquisa);

	Long pesquisarServidorCertificadoDigitalCount(String parametro,
			FccCentroCustos fccCentroCustos);
	
	
	RapServidoresVO pesquisarProfissionalPorServidor(RapServidores servidor);
	
	List<RapServidoresVO> pesquisarProfissionaisPorVinculoMatriculaNome(String strPesquisa, Integer count);
	
	Long pesquisarProfissionaisPorVinculoMatriculaNomeCount(String strPesquisa, Integer count);
	
	VRapServidorConselho obterValoresPrescricaoMedica(Integer matricula, Short vinCodigo);
	

	Long pesquisarServidorConselhoNroRegNaoNuloListaSiglaCount(
			List<String> listaSigla, Object objPesquisa);
	
	RapPessoalServidorVO obterValoresPrescricaoMedicaPessoaServidor(Integer matriculaServidorVerificado, Short vinCodigoServidorVerificado);

	Boolean verificarServidorExiste(Short codigo, Integer matricula);
	
	Object[] obterVRapPessoaServidorPorVinCodigoMatricula(Integer matricula, Short vinCodigo);
	List<Object[]> pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNome(List<Short> vinCodigos, List<Integer> cctCodigos, Integer matriculaVinCodigo, String nome);
	Long pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNomeCount(List<Short> vinCodigos, List<Integer> cctCodigos, Integer matriculaVinCodigo, String nome);
	
	Servidor obterVRapPessoaServidorPorVinCodMatricula(Integer matricula, Short vinCodigo);
	
	/**
	 * #39000 - Serviço que busca ultima solicitacao de exames
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	Boolean existeServidorCategoriaProfMedico(Integer matricula, Short vinculo) throws ApplicationBusinessException;
	
	VRapServidorConselho usuarioRelatoriosDefinitivos(Integer matricula, Short vinCodigo, String[] siglas);

	boolean verificarProfissionalPossuiCBOAnestesista(Short vinCodigo, Integer matricula, String[] siglasConselhos, String[] codigosTipoInformacoes); 

    public RapServidores obterServidorComPessoaFisicaByUsuario(String login);
	List<RapServidoresProcedimentoTerapeuticoVO> obterListaServidoresAtivos(String descricao);
    
	Long obterListaServidoresAtivosCount(String descricao);
	/**
	 * #43464 - Obtem Servidores para Suggestion Box
	 * @param parametro {@link String}
	 * @return {@link List} de {@link RapServidores}
	 */
	List<RapServidores> obterServidorPorMatriculaVinculoOuNome(String parametro);

	/**
	 * #43464 - Obtem contagem de Servidores para Suggestion Box
	 * @param parametro {@link String}
	 * @return Count {@link Long} 
	 */
	Long obterServidorPorMatriculaVinculoOuNomeCount(String parametro);



	List<RapServidores> pesquisarServidorPorVinculoOuMatriculaOuNome(String objPesquisa);

	Long pesquisarServidorPorVinculoOuMatriculaOuNomeCount(String objPesquisa);
    
	List<RapServidores> listarTodosServidoresOrdernadosPorNome();

	/**
	 * 43446 C1 Todos os tecnicos disponiveis para uma area tecnica de avaliacao.
	 * @param ataSeq
	 * @return
	 */
	List<RapServidores> obterServidorPorAreaTecnicaAvaliacao(Integer ataSeq);

	/**
	 * #43446 C2 Tecnicos escolhidos para realizar a analise tecnica.
	 * @param recebimento
	 * @param itemRecebimento
	 * @return
	 */
	List<RapServidores> obterServidorTecnicoPorItemRecebimento(Integer recebimento, Integer itemRecebimento);
	
	RapPessoaTipoInformacoes obterRapPessoa(Integer codigo);
    
    public RapPessoaTipoInformacoes obterTipoInformacao(Integer pesCodigo, Short seqTipoInf);

	List<RapServidores> pesquisarServidorPorSituacaoAtivoComUsuario(Object strPesq) throws BaseException;


	/**
	 * * #43033 P1 
	 * Consultas para obter CURSOR cur_pre
	 * Substitui CURSOR cur_pre em P1
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	List<CursorCurPreVO> obterCursorCurPreVO(Integer matricula, Short vinCodigo);
	
	/**
	 * * #43033 P2 
	 * Consultas para obter CURSOR cur_rap
	 * Substitui CURSOR cur_rap em P2
	 * @param pMatricula
	 * @param pVinCodigo
	 * @return
	 */
	List<CursorCurPreVO> obterCursorCurRapVO(Integer pMatricula, Short pVinCodigo);

	List<VRapPessoaServidorVO> pesquisarPessoasServidores(String pesquisa) throws ApplicationBusinessException;
	Long pesquisarPessoasServidoresCount(String pesquisa) throws ApplicationBusinessException;
	
	Long obterCountProfissionaisHospital(RapServidores profissional, RapVinculos vinculo, RapConselhosProfissionais conselho);
	
	List<ProfissionalHospitalVO> obterListaProfissionaisHospital(RapServidores profissional, RapVinculos vinculo, RapConselhosProfissionais conselho, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);

	boolean permiteSerResponsSolicExame(Short vinCodigo, Integer matricula) throws ApplicationBusinessException;
	
    /**
     * #8674 - Obtem lista de profissionais para suggestion box 
     */
	List<RapServidores> obterListaProfissionalPorMatriculaVinculoOuNome(String parametro);

	/**
     * #8674 - Obtem count de profissionais para suggestion box 
     */
	Long obterCountProfissionalPorMatriculaVinculoOuNome(String parametro);

	/**
     * #8674 - Obtem lista de vinculos para suggestion box 
     */
	List<RapVinculos> obterListaVinculoPorCodigoOuDescricao(String parametro);
	
	/**
     * #8674 - Obtem count de vinculos para suggestion box 
     */
	Long obterCountVinculoPorCodigoOuDescricao(String parametro);
	
	/**
     * #8674 - Obtem lista de conselhos para suggestion box 
     */
	List<RapConselhosProfissionais> obterListaConselhoPorCodigoOuSigla(String parametro);


	/**
     * #8674 - Obtem count de conselhos para suggestion box 
     */
	Long obterCountConselhoPorCodigoOuSigla(String parametro);



	List<RapServidores> pesquisarServidoresParaSuggestion(Object objPesquisa);

	Long pesquisarServidoresParaSuggestionCount(Object objPesquisa);

	List<RapServidores> listarServidoresComPessoaFisicaPorEquipe(String parametro,AghEquipes equipe);
	
}