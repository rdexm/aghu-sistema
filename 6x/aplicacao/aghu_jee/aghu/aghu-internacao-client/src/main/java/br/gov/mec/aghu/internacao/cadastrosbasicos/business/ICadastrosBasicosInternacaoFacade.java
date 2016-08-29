package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.internacao.vo.AinLeitosVO;
import br.gov.mec.aghu.internacao.vo.AinQuartosVO;
import br.gov.mec.aghu.internacao.vo.ProfConveniosListVO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnids;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenio;
import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinCaracteristicaLeito;
import br.gov.mec.aghu.model.AinDiariasAutorizadas;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinObservacoesPacAlta;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinTipoCaracteristicaLeito;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.AinTiposMvtoInternacao;
import br.gov.mec.aghu.model.FatTiposDocumento;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.cups.ImpImpressora;

@SuppressWarnings({"PMD.ExcessiveClassLength"})
public interface ICadastrosBasicosInternacaoFacade extends Serializable {

	public AinQuartos atualizarQuarto(final AinQuartos quarto, final boolean flush) throws ApplicationBusinessException;

	/**
	 * 
	 * @dbtables AinQuartos select
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param numero
	 * @param clinica
	 * @param excInfec
	 * @param consCli
	 * @return
	 */

	public List<AinQuartos> pesquisaQuartos(final Integer firstResult, final Integer maxResults, final String orderProperty,
			final boolean asc, final Short codigoQuartoPesquisa, final AghClinicas clinicaPesquisa, final DominioSimNao excInfecPesquisa,
			final DominioSimNao consCliPesquisa, final String descricao);

	List<AinQuartosVO> pesquisaQuartosNew(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Short numero,
			AghClinicas clinica, DominioSimNao excInfec, DominioSimNao consCli, String descricao);

	public Long pesquisaQuartosCount(final Short codigoQuartoPesquisa, final AghClinicas clinicaPesquisa,
			final DominioSimNao excInfecPesquisa, final DominioSimNao consCliPesquisa, final String descricao);

	/**
	 * 
	 * @dbtable AinQuartos select
	 * 
	 * @param ainQuartosCodigo
	 * @return
	 */

	public AinQuartos obterQuarto(final Short ainQuartosCodigo);

	/**
	 * 
	 * @dbtables AinQuartos select
	 * 
	 * @param strPesquisa
	 * @return
	 */

	public List<AinQuartos> pesquisarQuartos(final String strPesquisa);

	/**
	 * Método para obter a descrição completa do quarto (numero, andar, ala,
	 * descrição da unidade funcional)
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * @dbtables AinQuartos select
	 * 
	 * @param numeroQuarto
	 * @return Descrição completa do quarto
	 */
	public String obterDescricaoCompletaQuarto(final Short numeroQuarto);

	public void persistirQuarto(final AinQuartos quarto, final List<AinLeitosVO> leitosQuarto) throws ApplicationBusinessException;

	public void validarAtualizacaoLeito(final AinLeitos leito) throws ApplicationBusinessException;

	public List<AinQuartos> pesquisarQuartoSolicitacaoInternacao(final String strPesquisa, final DominioSexo sexoPaciente);

	public void validarLeitoExistente(final String idLeitos, final AinQuartos ainQuartos) throws ApplicationBusinessException;

	public void validaCaracteristicaPrincipal(final List<AinCaracteristicaLeito> ainCaracteristicas) throws ApplicationBusinessException;

	public void validarQuartoExistente(final String descricao) throws ApplicationBusinessException;

	public void desatacharQuarto(final AinQuartos ainQuartos);

	/**
	 * 
	 * @dbtables AghEspecialidades select
	 * 
	 * @param parametro
	 * @return
	 */

	public List<AghEspecialidades> pesquisarEspecialidadeSiglaEDescricao(final Object parametro);

	/**
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param parametro
	 * @return
	 */

	public List<AinTipoCaracteristicaLeito> pesquisarTiposCaracteristicasPorCodigoOuDescricao(final Object parametro);

	/**
	 * Valida se existe clínica com o código informado.
	 * 
	 * @dbtables AghClinicas select
	 * 
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	public boolean codigoClinicaExistente(final Integer codigo);

	/**
	 * Método responsável por inserir uma clínica
	 * 
	 * @dbtables AghClinicas select,insert
	 * 
	 * @param clinica
	 * @throws ApplicationBusinessException
	 */

	public void criarClinica(final AghClinicas clinica) throws ApplicationBusinessException;

	/**
	 * Método responsável por atualizar uma clínica
	 * 
	 * @dbtables AghClinicas select,update
	 * 
	 * @param clinica
	 * @throws ApplicationBusinessException
	 */

	public void atualizarClinica(final AghClinicas clinica) throws ApplicationBusinessException;

	/**
	 * Apaga uma clínica do banco de dados.
	 * 
	 * @dbtables AghClinicas delete
	 * 
	 * @param clinica
	 *            Clínica a ser removida.
	 * @throws ApplicationBusinessException
	 */

	public void removerClinica(final Integer codigo) throws ApplicationBusinessException;

	/**
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param ainTiposCaracteristicaLeitosCodigo
	 * @return
	 */

	public AinTipoCaracteristicaLeito obterTiposCaracteristicaLeitos(final Integer ainTipoCaracteristicaLeitoCodigo);

	/**
	 * Método responsável pela persistência de um tipo de característica de
	 * leito.
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select,insert,update
	 * 
	 * @param tipo
	 *            de característica de leito.
	 * @throws ApplicationBusinessException
	 */

	public void persistirTiposCaracteristicaLeito(final AinTipoCaracteristicaLeito ainTipoCaracteristicaLeito)
			throws ApplicationBusinessException;

	/**
	 * Apaga um tipo de característica de leito do banco de dados.
	 * 
	 * @dbtables AinTipoCaracteristicaLeito delete
	 * 
	 * @param codigo
	 *            .
	 * @throws ApplicationBusinessException
	 */
	public void removerTiposCaracteristicaLeito(final Integer codigo) throws ApplicationBusinessException;

	/**
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	public Long pesquisaTiposCaracteristicaLeitoCount(final Integer codigo, final String descricao);

	/**
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @return
	 */

	public List<AinTipoCaracteristicaLeito> pesquisaTiposCaracteristicaLeito(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final Integer codigoPesquisaTiposCaracteristicaLeito,
			final String descricaoPesquisaTiposCaracteristicaLeito);

	/**
	 * Retorna uma lista de tipos de caracteríticas de leitos
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param parametro
	 * @return
	 */

	public List<AinTipoCaracteristicaLeito> pesquisarTipoCaracteristicaPorCodigoOuDescricao(final String string);

	/**
	 * Método responsável pela persistência.
	 * 
	 * @param tipoAltaMedica
	 * @throws ApplicationBusinessException
	 */

	public void persistirTipoAltaMedica(final AinTiposAltaMedica tipoAltaMedica, final boolean edicaoOuInclusao)
			throws ApplicationBusinessException;

	/**
	 * Retorna um TipoAltaMedica com base na chave primária.
	 * 
	 * @param seq
	 * @return
	 */

	public AinTiposAltaMedica obterTipoAltaMedica(final String codigo);

	/**
	 * Método responsável por remover um registro do tipo TipoAltaMedica.
	 * 
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */

	public void removerTipoAltaMedica(final String tipoAlta) throws ApplicationBusinessException;

	public Long pesquisaTiposAltaMedicaCount(final String codigo, final MpmMotivoAltaMedica motivoAltaMedica, final DominioSituacao situacao);

	/**
	 * Busca tipos de altas médicas, conformes os parâmetros passados.
	 * 
	 * @param codigo
	 * @param motivoAltaMedica
	 * @param indSituacao
	 * @return List<AinTiposAltaMedica>
	 */

	public List<AinTiposAltaMedica> pesquisaTiposAltaMedica(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final String codigo, final MpmMotivoAltaMedica motivoAltaMedica,
			final DominioSituacao situacao);

	/**
	 * Metodo que busca objetos MotivosAltasMedicas pela descricao OU seq.
	 */

	public List<MpmMotivoAltaMedica> pesquisarMotivosAltaMedica(final String strPesq);

	/**
	 * Método resposável por buscar um Tipo Alta Médica, conforme a descrição
	 * passada como parêmetro. É utilizado pelo converter
	 * MpmMotivoAltaMedicasConverter.
	 * 
	 * @param descricao
	 * @return MpmMotivoAltaMedicas
	 */

	public MpmMotivoAltaMedica pesquisarMotivosAltaMedicaPorDescricao(final String valor);

	/**
	 * Método resposável por buscar um Tipo Alta Médica, conforme a string
	 * passada como parêmetro, que é comparada com o codigo
	 * 
	 * @param codigo
	 *            , idsIgnorados (ids de tipos alta medica que não devem ser
	 *            retornados pela consulta)
	 * @return Tipo Alta Médica
	 */

	public AinTiposAltaMedica pesquisarTipoAltaMedicaPorCodigo(final String strPesquisa, final String[] idsIgnorados);

	/**
	 * Método resposável por buscar um Tipo Alta Médica, conforme a string
	 * passada como parâmetro, que é comparada com o codigo e a descricao do
	 * Tipo Alta Médica É utilizado pelo converter AinTiposAltaMedicaConverter.
	 * 
	 * @param descricao
	 *            ou codigo, idsFiltrados (ids de tipos alta medica que podem
	 *            estar entre os retornados pela consulta)
	 * @return Lista de Tipos Alta Médica
	 */

	public List<AinTiposAltaMedica> pesquisarTipoAltaMedicaPorCodigoEDescricao(final String param, final String[] idsFiltrados);

	public List<AinTiposAltaMedica> pesquisarTipoAltaMedicaPorCodigoEDescricao(final String codDescLov);

	/**
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */

	public AinTiposMovimentoLeito obterTipoSituacaoLeito(final short shortValue);

	/**
	 * Método responsável pela persistência de um tipo de situação de leito.
	 * 
	 * @dbtables AinTiposMovimentoLeito select,insert
	 * 
	 * @param tipoSituacaoLeito
	 * @throws ApplicationBusinessException
	 */

	public void criarTipoSituacaoLeito(final AinTiposMovimentoLeito ainTipoSituacaoLeito) throws ApplicationBusinessException;

	/**
	 * Método responsável pela alteração de um tipo de situação de leito.
	 * 
	 * @dbtables AinTiposMovimentoLeito select,update
	 * 
	 * @param tipoSituacaoLeito
	 * @throws ApplicationBusinessException
	 */

	public void alterarTipoSituacaoLeito(final AinTiposMovimentoLeito ainTipoSituacaoLeito) throws ApplicationBusinessException;

	/**
	 * Apaga um tipo de situação de leito do banco de dados.
	 * 
	 * @dbtables AinTiposMovimentoLeito delete
	 * 
	 * @param tipoSituacaoLeito
	 *            Tipo de Situação de Leito a ser removido.
	 * @throws ApplicationBusinessException
	 */

	public void removerTipoSituacaoLeito(final Short codigo) throws ApplicationBusinessException;

	public Long pesquisaTipoSituacaoLeitoCount(final Short codigoPesquisaTipoSituacaoLeito,
			final String descricaoPesquisaTipoSituacaoLeito, final DominioMovimentoLeito grupoMvtoLeitoPesquisaTipoSituacaoLeito,
			final DominioSimNao indNecessitaLimpezaPesquisaTipoSituacaoLeito,
			final DominioSimNao indExigeJustificativaPesquisaTipoSituacaoLeito,
			final DominioSimNao indBloqueioPacientePesquisaTipoSituacaoLeito,
			final DominioSimNao indExigeJustLiberacaoPesquisaTipoSituacaoLeito);

	public List<AinTiposMovimentoLeito> pesquisaTipoSituacaoLeito(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final Short codigoPesquisaTipoSituacaoLeito,
			final String descricaoPesquisaTipoSituacaoLeito, final DominioMovimentoLeito grupoMvtoLeitoPesquisaTipoSituacaoLeito,
			final DominioSimNao indNecessitaLimpezaPesquisaTipoSituacaoLeito,
			final DominioSimNao indExigeJustificativaPesquisaTipoSituacaoLeito,
			final DominioSimNao indBloqueioPacientePesquisaTipoSituacaoLeito,
			final DominioSimNao indExigeJustLiberacaoPesquisaTipoSituacaoLeito);

	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * com situação de bloqueio limpeza (BL)<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * Ordernado por codigo.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */

	public AinTiposMovimentoLeito pesquisarTipoSituacaoBloqueioLimpezaPorDescricao(final String desc);

	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com o codigo passado por parametro.<br>
	 * E grupoMovimentoLeito: B, BI, BL, D.<br>
	 * 
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */

	public AinTiposMovimentoLeito pesquisarTipoSituacaoLeitoBloqueados(final Short codigo);

	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * E grupoMovimentoLeito: B, BI, BL, D.<br>
	 * Ordenado por grupoMovimentoLeito.
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */

	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoBloqueadosPorDescricao(final String descricaoStatus);
	
	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * E grupoMovimentoLeito: B, BI, BL, D.<br>
	 * E codigo.<br>
	 * Ordenado por grupoMovimentoLeito.
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */

	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoBloqueadosPorDescricaoOuCodigo(final String descricao);

	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com o codigo passado por parametro.<br>
	 * E grupoMovimentoLeito: L.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */

	public AinTiposMovimentoLeito pesquisarTipoSituacaoLeitoDesocupado(final Short codigo);

	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * E grupoMovimentoLeito: L.<br>
	 * Ordenado por grupoMovimentoLeito.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */

	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoDesocupadosPorDescricao(final String descricaoStatus);

	/**
	 * Retorna AinTiposMovimentoLeito para Tipos de Reserva. Filtros: codigo<br>
	 * grupoMovimentoLeito: L.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */

	public AinTiposMovimentoLeito pesquisarTipoSituacaoLeitoReservados(final Short codigo);

	/**
	 * Retorna AinTiposMovimentoLeito para Tipos de Reserva.<br>
	 * Filtros: Descricao,<br>
	 * grupoMovimentoLeito: R<br>
	 * Ordenado por Grupo movimento Leito.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */

	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoReservadosPorDescricao(final String descricaoTipoReserva);

	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * E grupoMovimentoSituacao: B, BI, BL, D, O, L, R.<br>
	 * Ordenado por codigo.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */

	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoPorDescricao(final String descricaoStatus);

	public AinTiposMvtoInternacao obterTiposMvtoInternacao(final Integer ainTiposMvtoInternacaoCodigo);

	/**
	 * Método responsável pela persistência de um tipo de movimento de
	 * internação.
	 * 
	 * @param tipo
	 *            de movimento de internação
	 * @throws ApplicationBusinessException
	 */

	public void persistirTiposMvtoInternacao(final AinTiposMvtoInternacao ainTiposMvtoInternacao) throws ApplicationBusinessException;

	/**
	 * Apaga um tipo de movimento de internação do banco de dados.
	 * 
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */

	public void removerTiposMvtoInternacao(final Integer codigo) throws ApplicationBusinessException;

	public Long pesquisaTiposMvtoInternacaoCount(final Integer codigoPesquisaTiposMvtoInternacao,
			final String descricaoPesquisaTiposMvtoInternacao);

	public List<AinTiposMvtoInternacao> pesquisaTiposMvtoInternacao(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final Integer codigoPesquisaTiposMvtoInternacao,
			final String descricaoPesquisaTiposMvtoInternacao);

	public AinObservacoesPacAlta obterObservacoesPacAlta(final Integer ainObservacoesPacAltaCodigo);

	/**
	 * Método responsável pela persistência de uma observação de alta do
	 * paciente.
	 * 
	 * @param observação
	 *            de alta do paciente
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */

	public void persistirObservacoesPacAlta(final AinObservacoesPacAlta ainObservacoesPacAlta) throws ApplicationBusinessException;

	/**
	 * Apaga uma observação de alta do paciente do banco de dados.
	 * 
	 * @param observação
	 *            de alta do paciente Observação de alta do paciente a ser
	 *            removida.
	 * @throws ApplicationBusinessException
	 */

	public void removerObservacoesPacAlta(final Integer codigo) throws ApplicationBusinessException;

	public Long pesquisaObservacoesPacAltaCount(final Integer codigoPesquisaObservacoesPacAlta,
			final String descricaoPesquisaObservacoesPacAlta, final DominioSituacao situacaoPesquisaObservacoesPacAlta);

	public List<AinObservacoesPacAlta> pesquisaObservacoesPacAlta(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final Integer codigoPesquisaObservacoesPacAlta,
			final String descricaoPesquisaObservacoesPacAlta, final DominioSituacao situacaoPesquisaObservacoesPacAlta);

	public List<AinObservacoesPacAlta> pesquisarObservacoesPacAlta(final Object parametro);

	public AinTiposCaraterInternacao obterTiposCaraterInternacao(final Integer ainTiposCaraterInternacaoCodigo);

	/**
	 * Método responsável pela persistência de um tipo de caráter de internação.
	 * 
	 * @param tipo
	 *            de caráter de internação
	 * @throws ApplicationBusinessException
	 */

	public void persistirTiposCaraterInternacao(final AinTiposCaraterInternacao ainTiposCaraterInternacao)
			throws ApplicationBusinessException;

	/**
	 * Apaga um tipo de caráter de internação do banco de dados.
	 * 
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */

	public void removerTiposCaraterInternacao(final Integer codigo) throws ApplicationBusinessException;

	public Long pesquisaTiposCaraterInternacaoCount(final Integer codigoPesquisaTiposCaraterInternacao,
			final String descricaoPesquisaTiposCaraterInternacao, final Integer codigoSUSPesquisaTiposCaraterInternacao,
			final DominioSimNao caraterPesquisaTiposCaraterInternacao);

	public List<AinTiposCaraterInternacao> pesquisaTiposCaraterInternacao(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final Integer codigoPesquisaTiposCaraterInternacao,
			final String descricaoPesquisaTiposCaraterInternacao, final Integer codigoSUSPesquisaTiposCaraterInternacao,
			final DominioSimNao caraterPesquisaTiposCaraterInternacao);

	/**
	 * Método responsável pela persistência de um tipo de unidade funcional.
	 * 
	 * @param tipo
	 *            de unidade funcional
	 * @throws ApplicationBusinessException
	 */

	public void persistirTiposUnidadeFuncional(final AghTiposUnidadeFuncional aghTiposUnidadeFuncional) throws ApplicationBusinessException;

	/**
	 * Apaga um tipo de unidade funcional do banco de dados.
	 * 
	 * @param tipo
	 *            de unidade funcional Tipo de unidade funcional a ser removida.
	 * @throws ApplicationBusinessException
	 */

	public void removerTiposUnidadeFuncional(final Integer codigoTipoUnidFunc) throws ApplicationBusinessException;

	public Long pesquisaTiposUnidadeFuncionalCount(final Integer codigoPesquisaTiposUnidadeFuncional,
			final String descricaoPesquisaTiposUnidadeFuncional);

	public List<AghTiposUnidadeFuncional> pesquisaTiposUnidadeFuncional(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final Integer codigoPesquisaTiposUnidadeFuncional,
			final String descricaoPesquisaTiposUnidadeFuncional);

	public List<AghTiposUnidadeFuncional> listarPorNomeOuCodigo(final String strPesquisa);

	/**
	 * Retorna uma acomodação com base na chave primária.
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param seq
	 * @return
	 */

	public AinAcomodacoes obterAcomodacao(final Integer ainAcomodacaoCodigo);

	/**
	 * Método responsável pela persistência de uma Acomodação.
	 * 
	 * @dbtables AinAcomodacoes select,insert,update
	 * 
	 * @param acomodacao
	 * @throws ApplicationBusinessException
	 */

	public void persistirAcomodacoes(final AinAcomodacoes ainAcomodacao) throws ApplicationBusinessException;

	public Long pesquisaAcomodacoesCount(final Integer codigoPesquisaAcomocadao, final String descricaoPesquisaAcomodacao);

	/**
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param parametro
	 * @return
	 */

	public List<AinAcomodacoes> pesquisarAcomodacoesPorCodigoOuDescricao(final Object parametro);

	public AinAcomodacoes obterAcomodacao(Object pk, Enum... fetchEnums);

	/**
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @return
	 */

	public List<AinAcomodacoes> pesquisarAcomodacoes(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final Integer codigoPesquisaAcomocadao, final String descricaoPesquisaAcomodacao);

	/**
	 * Método responsável pela remoção de uma Acomodação.
	 * 
	 * @dbtables AinAcomodacoes delete
	 * 
	 * @param acomodacao
	 * @throws ApplicationBusinessException
	 */

	public void removerAcomodacao(final Integer codigo) throws ApplicationBusinessException;

	/**
	 * Retorna uma lista de acomodações ordenado por descrição
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param parametro
	 * @return
	 */

	public List<AinAcomodacoes> pesquisarAcomodacoesPorCodigoOuDescricaoOrdenado(final Object objParam);

	/**
	 * Método responsável pela persistência de uma Instituição Hospitalar.
	 * 
	 * @param Instituição
	 *            Hospitalar
	 * @throws ApplicationBusinessException
	 */

	public void persistirInstituicao(final AghInstituicoesHospitalares instituicao) throws ApplicationBusinessException;

	public void removerInstituicaoHospitalar(final AghInstituicoesHospitalares instituicao) throws ApplicationBusinessException;
	
	/**
	 * Indica qual a instituição está usando o sistema.
	 * 
	 * @return
	 */
	public String recuperarNomeInstituicaoHospitalarLocal();

	public void cancelarImpressao() throws ApplicationBusinessException;

	public String obterDescricaoTipoMovtoInternacao(final Byte codigo);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricao(final Object objPesquisa);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(final String strPesquisa);

	public List<AghUnidadesFuncionaisVO> pesquisarUnidadeFuncionalVOPorCodigoEDescricao(final Object objPesquisa);

	public AghUnidadesFuncionaisVO popularUnidadeFuncionalVO(final AghUnidadesFuncionais unidadeFuncional);

	public AghUnidadesFuncionaisVO obterUnidadeFuncionalVO(final Short codigo);

	public Short pesquisarUnidadeFuncionalTriagemRecepcao(List<Short> listaUnfSeqTriagemRecepcao, Short unfSeqMicroComputador);

	public Long pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExamesCount(final String param);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(final Object objPesquisa);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricaoAtivasInativas(final Object objPesquisa);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricaoCaracteristicasInternacaoOuEmergencia(
			final Object objPesquisa);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoCaractInternacaoOuEmergenciaAtivasInativas(
			final Object objPesquisa);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExames(final String param);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalOrdenado(final String strPesquisa);
	
	void atualizarUnidadeFuncionalidade(AghUnidadesFuncionais unidadeFuncional, List<ConstanteAghCaractUnidFuncionais> caracteristicas) throws ApplicationBusinessException;
	void incluirUnidadeFuncional(final AghUnidadesFuncionais unidade, List<ConstanteAghCaractUnidFuncionais> caracteristicas) throws ApplicationBusinessException;
	void excluirUnidade(final Short seq)throws ApplicationBusinessException;

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExames(final Object param);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExamesOrdenadaDescricao(
			final Object param);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesAtivasOrdenadaDescricao(final Object param);

	/**
	 * método que obtem um leito através da chave primária.
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param id
	 * @return
	 */

	public AinLeitos obterLeitoPorId(final String id);

	AinLeitos obterLeitoPorId(String id, Enum... fields);

	/**
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */

	public List<AinLeitos> pesquisarLeitos(final Object paramentro);

	public boolean permitirInternar(final String leitoId);

	/**
	 * Método para obter a descrição completa do leito (leito, andar, ala,
	 * descrição da unidade funcional)
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param numeroQuarto
	 * @return Descrição completa do quarto
	 */

	public String obterDescricaoCompletaLeito(final String leitoId);

	public void verificarInternar(final String leitoId) throws ApplicationBusinessException;

	public AghEspecialidades obterEspecialidade(final Short seq);

	public AghEspecialidades obterAghEspecialidades(Short chavePrimaria, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	/**
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */

	public List<AinLeitos> pesquisarLeitosOrdenado(final Object paramentro);

	/**
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param leito
	 * @return
	 */

	public List<AinLeitos> pesquisaLeitoPeloLeitoId(final String leito);

	public ImpImpressora obterImpressora(final AghParametros aghParam, final TipoDocumentoImpressao tipoImpressora)
			throws ApplicationBusinessException;

	public ImpImpressora obterImpressora(final Short unfSeq, final TipoDocumentoImpressao tipoImpressora)
			throws ApplicationBusinessException;

	/**
	 * @param strPesquisa
	 * @param idadePaciente
	 * @return
	 */

	public List<AghEspecialidades> pesquisarEspecialidadeSolicitacaoInternacao(final String strPesquisa, final Short idadePaciente);

	/**
	 * Pesquisa de especialidade genérica por nome ou código
	 * 
	 * @dbtables AghEspecialidades select
	 * @param strPesquisa
	 */

	public List<AghEspecialidades> pesquisarEspecialidadeGenerica(final String strPesquisa);
	
	
	public List<AghEspecialidades> pesquisarTodasEspecialidades(final String strPesquisa);
	
	public Long pesquisarEspecialidadeGenericaCount(final String strPesquisa);
	
	public Long pesquisarTodasEspecialidadesCount(String strPesquisa);

	/**
	 * 
	 * Lista as Especialidades pela sigla
	 * 
	 * @return
	 */

	public List<AghEspecialidades> pesquisarEspecialidadesInternasPorSigla(final Object paramPesquisa);

	/**
	 * 
	 * Lista as Especialidades pela sigla e nome
	 * 
	 * @return
	 */

	public List<AghEspecialidades> pesquisarEspecialidadesInternasPorSiglaENome(final Object paramPesquisa);

	public Long pesquisarEspecialidadesInternasPorSiglaENomeCount(final Object paramPesquisa);

	/**
	 * Retorna leito desocupado.
	 * 
	 * @param paramentro
	 * @return
	 */

	public AinLeitos obterLeitoDesocupado(final String leito);

	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @param paramentro
	 * @return
	 */

	public List<AinLeitos> pesquisarLeitosDesocupados(final String paramentro);

	public List<AinLeitos> pesquisarLeitosPorSituacoesDoLeito(Object strParamentro, DominioMovimentoLeito[] situacoesLeito);

	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @param paramentro
	 * @return
	 */

	public AinLeitos obterLeitoBloqueado(final String leito);

	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @param paramentro
	 * @return
	 */

	public List<AinLeitos> pesquisarLeitosBloqueados(final Object paramentro);

	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @param parametro
	 * @return
	 */

	public List<AinLeitos> pesquisarLeitosDesocupadosPorLeito(final Object parametro);

	public Short buscarSeqUnidadeFuncionalSeqDoLeito(final String leitoID);

	public Short buscarSeqUnidadeFuncionalSeqDoQuarto(final Short qrtNumero);

	/**
	 * Método usado para remover as diárias da internação.
	 * 
	 * @param acompanhantesInternacaos
	 * @throws ApplicationBusinessException
	 */

	public void removerDiariasAutorizada(final List<AinDiariasAutorizadas> diariasAutorizadas) throws ApplicationBusinessException;

	/**
	 * Pesquisa de especialidade genérica por nome ou código levando em conta a
	 * idade do paciente internado e o índice de especialidade interna
	 * 
	 * Pesquisa utilizada na tela de internação.
	 * 
	 * @dbtables AghEspecialidades select
	 * @param strPesquisa
	 *            , idadePaciente
	 */

	public List<AghEspecialidades> pesquisarEspecialidadeInternacao(final String strPesquisa, final Short idadePaciente,
			final DominioSimNao indUnidadeEmergencia);

	/**
	 * Retorna uma lista de Diarias Hospitalares recebendo o codigo da
	 * internação como parametro
	 * 
	 * @param seqInternacao
	 * @return
	 */

	public List<AinDiariasAutorizadas> pesquisarDiariaPorCodigoInternacao(final Integer seqInternacao);

	public String recuperarNomeInstituicaoLocal();

	public List<FatTiposDocumento> obterTiposDocs(final String seqDesc);

	/**
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param matricula
	 * @param vinculo
	 * @param nome
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfEspecialidadesCRUD#pesquisarRapServidores(java.lang.Integer,
	 *      java.lang.Integer, java.lang.String, boolean, java.lang.Integer,
	 *      java.lang.Integer, java.lang.String)
	 */

	public List<RapServidores> pesquisarRapServidores(final Integer firstResult, final Integer maxResults, final String orderProperty,
			final boolean asc, final Integer matricula, final Integer vinculo, final String nome);

	/**
	 * @param matricula
	 * @param vinculo
	 * @param nome
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfEspecialidadesCRUD#pesquisarRapServidoresCount(java.lang.Integer,
	 *      java.lang.Integer, java.lang.String)
	 */
	public Long pesquisarRapServidoresCount(final Integer matricula, final Integer vinculo, final String nome);

	/**
	 * @param profEspecialidades
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfEspecialidadesCRUD#persistirProfEspecialidades(java.util.List)
	 */

	public void persistirProfEspecialidades(final List<AghProfEspecialidades> profEspecialidades, RapServidoresId rapServidorId) throws ApplicationBusinessException;

	/**
	 * @param profEspecialidades
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfEspecialidadesCRUD#validarDados(br.gov.mec.aghu.model.AghProfEspecialidades)
	 */
	public void validarDados(final AghProfEspecialidades profEspecialidades) throws ApplicationBusinessException;

	/**
	 * @param paramPesquisa
	 * @param ordemPorSigla
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfEspecialidadesCRUD#listarEspecialidadesAtivasPorSiglaOuDescricao(java.lang.Object,
	 *      boolean)
	 */
	public List<AghEspecialidades> listarEspecialidadesAtivasPorSiglaOuDescricao(final Object paramPesquisa, final boolean ordemPorSigla);

	/**
	 * @param strPesquisa
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfEspecialidadesCRUD#pesquisarEspecialidadePorSiglaNome(java.lang.Object)
	 */

	public List<AghEspecialidades> pesquisarEspecialidadePorSiglaNome(final Object strPesquisa);

	public Long pesquisarEspecialidadePorSiglaNomeCount(final Object strPesquisa);

	/**
	 * @param aghProfEspecialidadesId
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfEspecialidadesCRUD#obterProfEspecialidades(br.gov.mec.aghu.model.AghProfEspecialidadesId)
	 */

	public AghProfEspecialidades obterProfEspecialidades(final AghProfEspecialidadesId aghProfEspecialidadesId);

	/**
	 * @param rapServidor
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfissionaisEquipeCRUD#persistirRapServidor(br.gov.mec.aghu.model.RapServidores)
	 */

	public void persistirRapServidor(final RapServidores rapServidor) throws ApplicationBusinessException;

	public Integer pesquisaProfConveniosListCount(final Integer vinCodigo, final Integer matricula, final String nome, final Long cpf,
			final String siglaEspecialidade);

	public List<ProfConveniosListVO> pesquisaProfConvenioslist(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Integer vinCodigo, final Integer matricula, final String nome,
			final Long cpf, final String siglaEspecialidade);

	/**
	 * @param matricula
	 * @param vinculo
	 * @param esp
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfConveniosCRUD#obterAghProfEspecialidades(java.lang.Integer,
	 *      java.lang.Integer, java.lang.Integer)
	 */

	public AghProfEspecialidades obterAghProfEspecialidades(final Integer matricula, final Integer vinculo, final Integer esp);

	/**
	 * @param item
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfConveniosCRUD#removerAghProfEspConvenios(br.gov.mec.aghu.model.AghProfissionaisEspConvenio)
	 */

	public void removerAghProfEspConvenios(final AghProfissionaisEspConvenio item) throws ApplicationBusinessException;

	public void inserirAghProfEspConvenios(AghProfissionaisEspConvenio aghProfissionaisEspConvenio) throws ApplicationBusinessException;

	public void atualizarAghProfEspConvenios(AghProfissionaisEspConvenio aghProfissionaisEspConvenio) throws ApplicationBusinessException;

	/**
	 * @param matricula
	 * @param vinculo
	 * @param esp
	 * @param convenio
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfConveniosCRUD#verificarExclusao(java.lang.Integer,
	 *      java.lang.Integer, java.lang.Integer, java.lang.Short)
	 */

	public Boolean verificarExclusao(final Integer matricula, final Integer vinculo, final Integer esp, final Short convenio);

	public List<AghOrigemEventos> pesquisarOrigemEventoPorCodigoEDescricao(final Object objPesquisa);

	public AghOrigemEventos obterOrigemInternacao(final Short codigo);

	public AinInternacao obterInternacao(final Integer codigo);

	public void atualizarListaDiariasAutorizadas(final List<AinDiariasAutorizadas> listaDiariasAutorizadas,
			final List<AinDiariasAutorizadas> listaDiariasOld) throws ApplicationBusinessException;

	public List<AghImpressoraPadraoUnids> obterAghImpressoraPadraoUnids(final Short seq);

	public void validaCamposImpressoraPadrao(final TipoDocumentoImpressao tipoImpressao) throws ApplicationBusinessException;

	public void incluirImpressora(final List<AghImpressoraPadraoUnids> listaImpressoras,
			final List<AghImpressoraPadraoUnids> listaImpressorasOld, final AghUnidadesFuncionais unidade)
			throws ApplicationBusinessException;

	/**
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigoEspecialidade
	 * @param nomeEspecialidade
	 * @param siglaEspecialidade
	 * @param codigoEspGenerica
	 * @param centroCusto
	 * @param clinica
	 * @param situacao
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.EspecialidadeCRUD#pesquisarEspecialidades(java.lang.Integer,
	 *      java.lang.Integer, java.lang.String, boolean, java.lang.Short,
	 *      java.lang.String, java.lang.String, java.lang.Short,
	 *      java.lang.Integer, java.lang.Integer,
	 *      br.gov.mec.aghu.dominio.DominioSituacao)
	 */

	public List<AghEspecialidades> pesquisarEspecialidades(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final Short codigoEspecialidade, final String nomeEspecialidade, final String siglaEspecialidade,
			final Short codigoEspGenerica, final Integer centroCusto, final Integer clinica, final DominioSituacao situacao);

	/**
	 * @param codigo
	 * @param nomeEspecialidade
	 * @param siglaEspecialidade
	 * @param codigoEspGenerica
	 * @param centroCusto
	 * @param clinica
	 * @param situacao
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.EspecialidadeCRUD#pesquisarEspecialidadesCount(java.lang.Short,
	 *      java.lang.String, java.lang.String, java.lang.Short,
	 *      java.lang.Integer, java.lang.Integer,
	 *      br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	public Long pesquisarEspecialidadesCount(final Short codigo, final String nomeEspecialidade, final String siglaEspecialidade,
			final Short codigoEspGenerica, final Integer centroCusto, final Integer clinica, final DominioSituacao situacao);

	/**
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.EspecialidadeCRUD#gerarNovaEspecialidade()
	 */
	public AghEspecialidades gerarNovaEspecialidade();

	/**
	 * @param especialidade
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.EspecialidadeCRUD#persistirEspecialidade(br.gov.mec.aghu.model.AghEspecialidades)
	 */

	public void persistirEspecialidade(final AghEspecialidades especialidade) throws ApplicationBusinessException;

	/**
	 * @param seq
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.EspecialidadeCRUD#obterEspecialidadeAtiva(short)
	 */

	public AghEspecialidades obterEspecialidadeAtiva(final short seq);

	/**
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.EspecialidadeCRUD#obterEspecialidadePediatria()
	 */
	public List<AghEspecialidades> obterEspecialidadePediatria();

	/**
	 * @param strPesquisa
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.LeitosCRUD#pesquisarLeitosAtivosDesocupados(java.lang.String)
	 */

	public List<AinLeitos> pesquisarLeitosAtivosDesocupados(final String strPesquisa);

	public AghAla buscaAghAlaPorId(final String codigo);

	public void excluirAghAla(final String codigo) throws ApplicationBusinessException;

	public List<AghAla> pesquisaAlaList(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final String codigo, final String descricao);

	public Long pesquisaAlaListCount(final String codigo, final String descricao);

	public AghAla persistirAghAla(final AghAla ala, final boolean isUpdate) throws ApplicationBusinessException;

	public void removerOrigemEventos(final AghOrigemEventos origemEventos) throws ApplicationBusinessException;

	public void persistirOrigemEventos(final AghOrigemEventos origemEventos) throws ApplicationBusinessException;

	public void validaLeitoMedidaPreventiva(Short numero, Short codigoMvtLeito) throws ApplicationBusinessException;

	public Integer pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(Object param);

	public List<AghProfEspecialidades> listarProfEspecialidadesPorServidor(RapServidores usuarioResponsavel);

	List<AinLeitosVO> pesquisaLeitosPorNroQuarto(Short nroQuarto);

	List<AinLeitos> pesquisaAinLeitosPorNroQuarto(Short nroQuarto);

	List<AinCaracteristicaLeito> obterCaracteristicasDoLeito(String ltoId);

	AinLeitos obterLeitoInternacaoPorId(String id);

	AinQuartos obterQuarto(Short ainQuartosCodigo, Enum[] innerFields, Enum[] leftFields);

	AinLeitos obterLeitoPorId(String id, Enum[] innerFields, Enum[] leftFields);

	AinQuartos obterQuartosLeitosPorId(Short numero);

	List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(final String strPesquisa);

	public Long pesquisaCount(Short codigo, String descricao, String sigla,
			AghClinicas clinica, FccCentroCustos centroCusto,
			AghUnidadesFuncionais unidadeFuncionalPai,
			DominioSituacao situacao, String andar, AghAla ala);

	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(
			Integer firstResult, Integer maxResults, String orderProperty,
			Boolean asc, Short codigo, String descricao, String sigla,
			AghClinicas clinica, FccCentroCustos centroCusto,
			AghUnidadesFuncionais unidadeFuncionalPai,
			DominioSituacao situacao, String andar, AghAla ala);

	List<AinLeitos> getLeitosPorQuarto(AinQuartos quarto);

	void excluirLeitoSemMovimentacao(AinLeitos leito)
			throws ApplicationBusinessException;

	Boolean leitoPossuiMovimentacao(AinLeitosVO leito)
			throws ApplicationBusinessException;

	List<AghEspecialidades> obterEspecialidadePorSiglas(List<String> siglas);

	Boolean verificaExisteImpressoraPadrao(Short unfSeq,
			TipoDocumentoImpressao tipoImpressora)
			throws ApplicationBusinessException;
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalInternacaoAtiva(
			String strPesquisa);	

}