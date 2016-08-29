package br.gov.mec.aghu.internacao.pesquisa.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioGrupoConvenioPesquisaLeitos;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaPacComAlta;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaPacientesAdmitidos;
import br.gov.mec.aghu.dominio.DominioOrigemPaciente;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.dominio.DominioTipoAlta;
import br.gov.mec.aghu.internacao.administracao.business.VAinPesqPacOV;
import br.gov.mec.aghu.internacao.business.vo.InternacaoAtendimentoUrgenciaPacienteVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.CidInternacaoVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.DadosInternacaoVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ExtratoLeitoVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ExtratoPacienteVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisaLeitosVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisaPacientesAdmitidosVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisaPacientesComPrevisaoAltaVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisarSituacaoLeitosVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ReferencialEspecialidadeProfissonalGridVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.LeitoDisponibilidadeVO;
import br.gov.mec.aghu.internacao.vo.PacienteProfissionalEspecialidadeVO;
import br.gov.mec.aghu.internacao.vo.PesquisaReferencialClinicaEspecialidadeVO;
import br.gov.mec.aghu.internacao.vo.QuartoDisponibilidadeVO;
import br.gov.mec.aghu.internacao.vo.VAinAltasVO;
import br.gov.mec.aghu.internacao.vo.VAinCensoVO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinAcompanhantesInternacao;
import br.gov.mec.aghu.model.AinAcompanhantesInternacaoId;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinObservacoesCenso;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.VAinDispVagas;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface IPesquisaInternacaoFacade extends Serializable {

	public boolean validar(Integer prontuario) throws ApplicationBusinessException;

	public String getNomePacienteProntuario(Integer prontuario);

	public List<DadosInternacaoVO> pesquisarDatas(Integer prontuario,
			Date dataInternacao);

	public Long pesquisarExtratoPacienteCount(Integer codigoInternacao,
			Date dataInternacao);

	
	public List<ExtratoPacienteVO> pesquisarExtratoPaciente(
			Integer firstResult, Integer maxResult, Integer codigoInternacao,
			Date dataInternacao);
	
	/**
	 * Localiza a unidade funcional associada a um quarto ou leito a partir dos
	 * parâmetros informados.
	 * 
	 * ORADB Function AINC_RET_UNF_SEQ.
	 * 
	 * @return Id da unidade funcional encontrada.
	 */
	public Short aincRetUnfSeq(Short pUnfSeq, Short pQuarto, String pLeito);

	/**
	 * Método responsável por realizar a query de consulta de disponibilidades
	 * de leitos.
	 * 
	 * @param idAcomodacao
	 * @param idClinica
	 * @param seqUnidadeFuncional
	 * @param idLeito
	 * @param numeroQuarto
	 * @return
	 */
	
	public List<LeitoDisponibilidadeVO> pesquisarDisponibilidadeLeitos(
			Integer firstResult, Integer maxResult, Integer idAcomodacao,
			Integer idClinica, Short seqUnidadeFuncional, String idLeito,
			Short numeroQuarto);
	
	public Long pesquisarDisponibilidadeLeitosCount(
			Integer idAcomodacao,
			Integer idClinica, Short seqUnidadeFuncional, String idLeito,
			Short numeroQuarto);	

	/**
	 * Verifica se é possível internar paciente no leito informado em função do
	 * sexo
	 */
	public void consistirSexoLeito(AipPacientes paciente, AinLeitos leito)
			throws ApplicationBusinessException;

	/**
	 * Verifica se é possível internar paciente no quarto informado em função do
	 * sexo
	 */
	public void consistirSexoQuarto(AipPacientes paciente, AinQuartos quarto)
			throws ApplicationBusinessException;

	
	public String buscarNomeUsual(Short pVinCodigo, Integer pMatricula);

	/**
	 * ORADB Function RAPC_BUSC_NRO_R_CONS
	 */
	public String buscarNroRegistroConselho(Short vinCodigo, Integer matricula);

	/**
	 * ORADB ainc_busca_senha_int
	 */
	public String buscaSenhaInternacao(Integer internacaoSeq);

	/**
	 * Verifica se uma unidade funcional tem determinada característica, se
	 * tiver retorna 'S' senão retorna 'N'.
	 * 
	 * ORADB Function AGHC_VER_CARACT_UNF.
	 * 
	 * @return Valor 'S' ou 'N' indicando se a característica pesquisada foi
	 *         encontrada.
	 */
	
	public DominioSimNao verificarCaracteristicaDaUnidadeFuncional(
			Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica);

	/**
	 * Retorna uma AinInternacao com base na chave primária.
	 * 
	 * @param seq
	 * @return AinInternacao
	 */
	
	public AinInternacao obterInternacao(Integer seq);

	public AinInternacao obterInternacaoComLefts(Integer seq);
		
	/**
	 * Retorna os acompanhantes na internação.
	 * 
	 * @param seq
	 *            da Internação.
	 * @return Lista de Acomponhantes
	 */
	
	public List<AinAcompanhantesInternacao> obterAcompanhantesInternacao(
			Integer seq);

	public AinAcompanhantesInternacao obterAinAcompanhantesInternacao(
			AinAcompanhantesInternacaoId id);

	/**
	 * Método para buscar a última internação de um paciente através do código
	 * do paciente.
	 * 
	 * @param codigoPaciente
	 * @return objeto AinInternacao
	 */
	// 
	public List<QuartoDisponibilidadeVO> pesquisarDisponibilidadeQuartosVO(
			Integer firstResult, Integer maxResult, Short nroQuarto,
			Integer clinica, Short unidade);

	/**
	 * Método para retornar a capacidade dos quartos.
	 * 
	 * @param quartosList
	 * @return
	 */
	public List<QuartoDisponibilidadeVO> pesquisarCapacIntQrt(
			List<QuartoDisponibilidadeVO> quartosList);

	/**
	 * Método que busca o total de pacientes internados por quarto.
	 * 
	 * @param quartoList
	 */
	public void pesquisarTotalIntQrt(List<QuartoDisponibilidadeVO> quartoList);

	/**
	 * Busca SolicTransfPacientes associadas a quartos e com IndSolicLeito igual
	 * a P
	 * 
	 * @param quartoList
	 * @return
	 */
	public List<QuartoDisponibilidadeVO> pesquisarTotSolTransPndQrt(
			List<QuartoDisponibilidadeVO> quartoList);

	/**
	 * Busca vagas por quarto.
	 * 
	 * @param quartoList
	 */
	public void pesquisarDispVagasQrt(List<QuartoDisponibilidadeVO> quartoList);

	/**
	 * Verifica se o quarto pertence a uma unidade de internação ORADB:
	 * Procedure AINP_TRAZ_QRT da AINF_DISP_LEITOS.PLL ORADB: View
	 * V_AIN_QUARTOS_TRANSF
	 */
	public void consistirQuarto(AinQuartos quarto) throws ApplicationBusinessException;

	/**
	 * Método para retornar todas Unidades Funcionais cadastradas que são
	 * unidade de internação.
	 * 
	 * @param Objeto
	 *            de Unidade Funcional para filtrar andar, ala e clínica
	 * @return Lista com todas Unidades Funcionais
	 */
	
	public List<VAinDispVagas> pesquisarVAinDispVagas(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AghClinicas clinica, final AghUnidadesFuncionais unidade);

	
	public Long pesquisarVAinDispVagasCount(final AghClinicas clinica,
			final AghUnidadesFuncionais unidade);

	/**
	 * Método que pesquisa as internações de um dado paciente, filtrando pelo
	 * prontuário
	 * 
	 * @param prontuario
	 * @return
	 */
	
	public List<AinInternacao> pesquisarInternacoesPorProntuarioUnidade(
			Integer prontuario);

	/**
	 * Retorna uma view VAinPesqPac com base na prontuario que correspode uma
	 * internação.
	 * 
	 * @param prontuario
	 * @return VAinPesqPac
	 */
	
	public VAinPesqPacOV pesquisaDetalheInternacao(Integer intSeq)
			throws ApplicationBusinessException;

	public Long pesquisaCidsInternacaoCount(Integer codInternacao);

	
	public List<CidInternacaoVO> pesquisaCidsInternacao(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codInternacao);

	/***
	 * Valida os critérios de pesquisa de para Pesquisa Pacientes Admitidos
	 * 
	 * @author Stanley Araujo
	 * @param codigoEspecialidade
	 *            - Código (chave) da especialidade
	 * @param codigoClinica
	 *            - Código (chave) da clínica
	 * @param codigoConvenio
	 *            - Código (chave) do convênio
	 * @param codigoPlano
	 *            - Código (chave) do plano
	 * @param dataInicial
	 *            - Data inicial para contagem
	 * @param dataFinal
	 *            - Data final para a contagem
	 * @param prontuario
	 *            - Número do prontuário do paciente
	 * @exception ApplicationBusinessException
	 *                - Se a especialidade é inválida
	 * @exception ApplicationBusinessException
	 *                - Se a clínica é inválida
	 * @exception ApplicationBusinessException
	 *                - Se o convênio é inválido
	 * @exception ApplicationBusinessException
	 *                - Se o plano é inválido
	 * @exception ApplicationBusinessException
	 *                - Se o número do prontuário é inválido
	 * @exception ApplicationBusinessException
	 *                - Se a data inicial for menor que a data final
	 * @exception ApplicationBusinessException
	 *                - Se a diferenção entre a data final e a inicial for
	 *                superior a 07(sete) dias
	 * @exception ApplicationBusinessException
	 *                - Se a diferenção entre a data final e a inicial for
	 *                superior a 31(trinta e um) dias
	 * */

	public void validaPesquisaPacientesAdmitidos(AghEspecialidades codigoEspecialidade,
			AghClinicas codigoClinica, Short codigoConvenio, Byte codigoPlano,
			Date dataInicial, Date dataFinal, Integer prontuario)
			throws ApplicationBusinessException;

	/***
	 * Cria um DetachedCriteria de acordo com os parâmetros de pesquisa
	 * 
	 * @author Stanley Araujo
	 * @param codigoEspecialidade
	 *            - Código(Chave) da especialidade
	 * @param origemPaciente
	 *            - Origem do Evento
	 * @param ordenacaoPesquisa
	 *            - Ordenação da pesquisa
	 * @param codigoClinica
	 *            - Código(cheve) da clínica
	 * @param codigoConvenio
	 *            - Código do convênio
	 * @param codigoPlano
	 *            - Código do plano associado ao convênio
	 * @param codigoPaciente
	 *            - Código do paciente
	 * @param dataInicial
	 *            - Data inicial para a pesquisa
	 * @param dataFinal
	 *            - Data final para a pesquisa
	 * @return Quantidade de pacientes admitidos
	 * */
	public Long pesquisaPacientesAdmitidosCount(AghEspecialidades codigoEspecialidade,
			DominioOrigemPaciente origemPaciente,
			DominioOrdenacaoPesquisaPacientesAdmitidos ordenacaoPesquisa,
			AghClinicas codigoClinica, Short codigoConveniosPlano,
			Byte codigoPlano, Date dataInicial, Date dataFinal,
			Integer codigoPaciente);

	/**
	 * Pesquisa os pacientes admitidos conforme os parâmetros informado.
	 * 
	 * @author Stanley Araujo
	 * @param codigoEspecialidade
	 *            - Código(Chave) da especialidade
	 * @param origemPaciente
	 *            - Origem do Evento
	 * @param ordenacaoPesquisa
	 *            - Ordenação da pesquisa
	 * @param codigoClinica
	 *            - Código(cheve) da clínica
	 * @param codigoConvenio
	 *            - Código do convênio
	 * @param codigoPlano
	 *            - Código do plano associado ao convênio
	 * @param codigoPaciente
	 *            - Código do paciente
	 * @param dataInicial
	 *            - Data inicial para a pesquisa
	 * @param dataFinal
	 *            - Data final para a pesquisa
	 * @param firstResult
	 *            - Primeiro resultado
	 * @param maxResult
	 *            - Máximo resultado
	 * @param orderProperty
	 *            -
	 * @param asc
	 *            -
	 * 
	 * @return Lista de pacientes com previsão de alta
	 * 
	 * */
	
	public List<PesquisaPacientesAdmitidosVO> pesquisaPacientesAdmitidos(
			AghEspecialidades codigoEspecialidade, DominioOrigemPaciente origemPaciente,
			DominioOrdenacaoPesquisaPacientesAdmitidos ordenacaoPesquisa,
			AghClinicas codigoClinica, Short codigoConvenio, Byte codigoPlano,
			Date dataInicial, Date dataFinal, Integer codigoPaciente,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	public void validaDefineWhere(Date dataInicio, Date dataFim,
			boolean altaAdministrativa, DominioTipoAlta tipoAlta,
			String tamCodigo) throws ApplicationBusinessException;

	public Long pesquisaPacientesComAltaCount(Date dataInicial,
			Date dataFinal, boolean altaAdministrativa,
			DominioTipoAlta tipoAlta, Short unidFuncSeq, Short espSeq,
			String tamCodigo);

	
	public List<VAinAltasVO> pesquisaPacientesComAlta(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Date dataInicial, Date dataFinal, boolean altaAdministrativa,
			DominioTipoAlta tipoAlta,
			DominioOrdenacaoPesquisaPacComAlta ordenacao, Short unidFuncSeq,
			Short espSeq, String tamCodigo);

	
	public BigDecimal buscaMatriculaConvenio(Integer prontuario,
			Short convenio, Byte plano);

	public String montaLocalAltaDePaciente(String ltoLtoId, Short qrtNumero,
			String andar, AghAla indAla);

	/**
	 * Valida a data inicial e final e verifica se a diferença entre elas é
	 * superior ao permitido
	 * 
	 * @author Stanley Araujo
	 * @param dataInicio
	 *            - Data de inicio da validação
	 * @param dataFinal
	 *            - Data de final da validação
	 * @throws ApplicationBusinessException
	 * @exception ApplicationBusinessException
	 *                - Se a data inicial for menor que a data final
	 * @exception ApplicationBusinessException
	 *                - Se a diferenção entre a data final e a inicial for
	 *                superior ao limite permitido
	 * */
	public void validarDiferencaDataInicialFinalSemEspecialidade(
			Date dataInicio, Date dataFinal) throws ApplicationBusinessException;

	/***
	 * Realiza a contagem de pacientes com previsão de alta em um intervalo de
	 * tempo
	 * 
	 * @author Stanley Araujo
	 * @param dataInicial
	 *            - Data inicial para contagem
	 * @param dataFinal
	 *            - Data final para a contagem
	 * @return Quantidade de pacientes com previsão de alta
	 * */
	public Long pesquisaPacientesComPrevisaoAltaCount(Date dataInicial,
			Date dataFinal);

	/***
	 * Realiza a pesquisa de pacientes com previsão de alta em um intervalo de
	 * tempo
	 * 
	 * 
	 * @author Stanley Araujo
	 * @param firstResult
	 *            - Primeiro resultado
	 * @param maxResult
	 *            - Máximo resultado
	 * @param orderProperty
	 *            -
	 * @param asc
	 *            -
	 * @param dataInicial
	 *            - Data inicial para a pesquisa
	 * @param dataFinal
	 *            - Data final para a pesquisa
	 * @return Lista de pacientes com previsão de alta
	 * */
	
	public List<PesquisaPacientesComPrevisaoAltaVO> pesquisaPacientesComPrevisaoAlta(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Date dataInicial, Date dataFinal);

	/**
	 * ORADB ainc_busca_ult_alta
	 */
	public Date buscaUltimaAlta(Integer internacaoSeq);

	public Long pesquisarInternacoesDoPacientePorProntuarioEDataInternacaoCount(
			Integer prontuario, Date dataInternacao);

	/**
	 * Metodo que busca internacoes de um paciente atraves do seu prontuario
	 * (obrigatorio) e sua data de internacao (opcional).
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param prontuario
	 *            - prontuario do paciente o qual desejamos buscar as
	 *            internacoes.
	 * @param dataInternacao
	 *            - data de internacao do paciente
	 * @return lista de internacoes do respectivo prontuario informado como
	 *         parametro.
	 */
	
	public List<AinInternacao> pesquisarInternacoesDoPacientePorProntuarioEDataInternacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer prontuario, Date dataInternacao);

	
	public List<InternacaoAtendimentoUrgenciaPacienteVO> pesquisarInternacaoAtendimentoUrgenciaPorPaciente(
			Integer codigoPaciente);

	/**
	 * Método para buscar todos registros de internação com o ID do projeto de
	 * pesquisa recebido por parâmetro.
	 * 
	 * @param seqProjetoPesquisa
	 * @return
	 */
	
	public List<AinInternacao> pesquisarInternacaoPorProjetoPesquisa(
			Integer seqProjetoPesquisa);

	/**
	 * Método para retornar todos objetos de internação de um determinado
	 * paciente
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	
	public List<AinInternacao> pesquisarInternacaoPorPaciente(
			Integer codigoPaciente);

	
	public Boolean verificarPacienteInternado(Integer codigoPaciente);

	
	public Boolean verificarPacienteHospitalDia(Integer codigoPaciente);

	public void consistirSexoLeito(Integer codigoPaciente, String idLeito)
			throws ApplicationBusinessException;

	public void consistirSexoQuarto(Integer codigoPaciente, Short quartoNumero)
			throws ApplicationBusinessException;

	
	public AinAtendimentosUrgencia obterPacienteAtendimentoUrgencia(
			Integer codigoPaciente);

	/**
	 * retorna o detalhe de um atendimento de urgência.
	 * 
	 * @dbtables AinAtendimentosUrgencia select
	 * 
	 * @param codigoAtendimentoUrgencia
	 * @return
	 */
	
	public AinAtendimentosUrgencia obterDetalheAtendimentoUrgencia(
			Integer codigoAtendimentoUrgencia) throws ApplicationBusinessException;

	/**
	 * @param leito
	 * @return
	 */
	
	public AinAtendimentosUrgencia obterDetalheAtendimentoUrgencia2(String leito);

	/**
	 * Valida se pelo menos um filtro foi informado pela pesquisa.
	 * 
	 * @param status
	 * @param acomodacao
	 * @param clinica
	 * @param convenio
	 * @param unidade
	 * @param leito
	 * @param grupoConvenio
	 * @param ala
	 * @param andar
	 * @param infeccao
	 * @throws ApplicationBusinessException
	 */
	public void validaDados(AinTiposMovimentoLeito status, AinAcomodacoes acomodacao, 
			AghClinicas clinica, FatConvenioSaude convenio, AghUnidadesFuncionais unidade, 
			AinLeitos leito, DominioGrupoConvenioPesquisaLeitos grupoConvenio, 
			AghAla ala, Integer andar, DominioSimNao infeccao) throws ApplicationBusinessException;

	/**
	 * Verifica qual data block deve ser executado 0 - VPL 1 - VPL1 2 - STP
	 * 
	 * @return
	 */
	public Integer verificarDataBlock(FatConvenioSaude convenio,
			DominioGrupoConvenioPesquisaLeitos grupoConvenio,
			DominioMovimentoLeito mvtoLeito);

	/**
	 * Retorna total de registros
	 * 
	 * @dbtables AinSolicTransfPacientes select
	 * 
	 * @return
	 */
	public Long pesquisarSolicitacoesTransferenciaPacientesCount();

	/**
	 * Cria a pesquisa de acordo com o Data Block STP do oracle forms
	 * 
	 * @dbtables AinSolicTransfPacientes select
	 * 
	 * @return
	 */
	
	public List<PesquisaLeitosVO> pesquisarSolicitacoesTransferenciaPacientes();

	/**
	 * Método que obtém a lista de leitos.
	 * 
	 * @dbtables VAinPesqLeitos select
	 * 
	 * @param status
	 * @param acomodacao
	 * @param clinica
	 * @param convenio
	 * @param unidade
	 * @param leito
	 * @param grupoConvenio
	 * @param ala
	 * @param andar
	 * @param infeccao
	 * @return
	 */
	
	public List<PesquisaLeitosVO> pesquisarLeitos(AinTiposMovimentoLeito status, AinAcomodacoes acomodacao, 
			AghClinicas clinica, FatConvenioSaude convenio, AghUnidadesFuncionais unidade, 
			AinLeitos leito, DominioGrupoConvenioPesquisaLeitos grupoConvenio, AghAla ala,
			Integer andar, DominioSimNao infeccao,
			DominioMovimentoLeito mvtoLeito, int dataBlock,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	/**
	 * Retorna total de registros
	 * 
	 * @param status
	 * @param acomodacao
	 * @param clinica
	 * @param convenio
	 * @param unidade
	 * @param leito
	 * @param grupoConvenio
	 * @param ala
	 * @param andar
	 * @param infeccao
	 * @return
	 */
	public Long pesquisarLeitosCount(AinTiposMovimentoLeito status, AinAcomodacoes acomodacao, 
			AghClinicas clinica, FatConvenioSaude convenio, AghUnidadesFuncionais unidade, 
			AinLeitos leito, DominioGrupoConvenioPesquisaLeitos grupoConvenio, AghAla ala,
			Integer andar, DominioSimNao infeccao,
			DominioMovimentoLeito mvtoLeito, int dataBlock);

	public Long pesquisaSituacaoLeitosCount(AghClinicas clinica);

	
	public List<PesquisarSituacaoLeitosVO> pesquisaSituacaoLeitos(
			AghClinicas clinicaParam, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	public Long pesquisarExtratoLeitoCount(String leito, Date data);

	
	public List<ExtratoLeitoVO> montarExtratoLeitoVO(String leito, Date data,
			Integer firstResult, Integer maxResult);

	/**
	 * Pesquisa internacoes de pacientes que possuam o atributo indSaidaPaciente
	 * == false e tambem pelos seguintes parametros:
	 * 
	 * @param prontuario
	 * @param pacCodigo
	 * @param pacNome
	 * @param espSeq
	 *            - id da especialidade
	 * @param ltoId
	 *            - id do leito
	 * @param qrtNum
	 *            - numero do quarto
	 * @param unfSeq
	 *            - id da unidade funcional
	 * @param matriculaProfessor
	 * @param vinCodigoProfessor
	 * 
	 * @return List<AinInternacao> - lista de internacoes conforme os criterios
	 *         de pesquisa
	 */
	
	public List<AinInternacao> pesquisarInternacoesAtivas(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Integer prontuario, Integer pacCodigo, String pacNome,
			Short espSeq, String leitoID, Short qrtNum, Short unfSeq,
			Integer matriculaProfessor, Short vinCodigoProfessor);

	public Long pesquisarInternacoesAtivasCount(Integer prontuario,
			Integer pacCodigo, String pacNome, Short espSeq, String leitoID,
			Short qrtNum, Short unfSeq, Integer matriculaProfessor,
			Short vinCodigoProfessor);

	/**
	 * ORADB Function AINC_BUSCA_UNID_INT.
	 * 
	 * @param leitoID
	 * @param qrtNumero
	 * @param unfSeq
	 * @return
	 */
	
	public Short buscarUnidadeInternacao(String leitoID, Short qrtNumero,
			Short unfSeq);

	
	public EspCrmVO pesquisarProfissionalPorEspecialidadeCRM(
			AghEspecialidades especialidade, String strPesquisa)
			throws ApplicationBusinessException;

	/**
	 * 
	 * Busca especialidades por Nome ou Sigla
	 * 
	 * @return Lista de especialidades
	 */
	
	public List<AghEspecialidades> pesquisarEspecialidadePorSiglaNome(
			Object strPesquisa);

	public Long pesquisarEspecialidadePorSiglaNomeCount(Object strPesquisa);

	
	public List<EspCrmVO> pesquisaProfissionalEspecialidade(
			AghEspecialidades especialidade, String strPesquisa)
			throws ApplicationBusinessException;

	public Integer pesquisaPacientesProfissionalEspecialidadeCount(
			AghEspecialidades especialidade, EspCrmVO profissional)
			throws ApplicationBusinessException;

	
	public List<PacienteProfissionalEspecialidadeVO> pesquisaPacientesProfissionalEspecialidade(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, AghEspecialidades especialidade, EspCrmVO profissional)
			throws ApplicationBusinessException;

	/**
	 * Método para pesquisar escalar de profissionais.
	 * 
	 * @param matriculaProfessor
	 * @param codigoProfessor
	 * @param seqEspecialidade
	 * @param codigoConvenioSaude
	 * @return
	 */
	
	public List<AinEscalasProfissionalInt> pesquisarEscalaProfissionalInt(
			Integer matriculaProfessor, Short codigoProfessor,
			Short seqEspecialidade, Short codigoConvenioSaude);

	public void validaDadosPesquisaReferencialClinicaEspecialidade(
			AghClinicas clinica) throws ApplicationBusinessException;

	public Long pesquisaReferencialClinicaEspecialidadeCount(
			AghClinicas clinica) throws ApplicationBusinessException;

	/**
	 * ORADB V_AIN_PES_REF_CLI_ESP
	 * 
	 * Esta view realiza o join de 4 SQLs, os quais serão transcritos como SQLs
	 * independentes e adicionados a mesma coleção, paga unificar os resultados.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param clinica
	 * 
	 * @return
	 */
	
	public List<PesquisaReferencialClinicaEspecialidadeVO> pesquisaReferencialClinicaEspecialidade(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghClinicas clinica) throws ApplicationBusinessException;

	/**
	 * 
	 * @return List<VAinCensoVO> - lista de VAinCensoVO conforme os criterios de
	 *         pesquisa.
	 * @throws ApplicationBusinessException
	 */
	
	public Object[] pesquisarCensoDiarioPacientes(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException;

	public Integer calcularIdadeDaData(Date data);

	/**
	 * Verifica se uma determinada internacao possui documentos pendentes.
	 * 
	 * @param intSeq
	 *            - id da internacao
	 * @return true - se existirem docs pendentes
	 */
	public boolean verificarDocumentosPendentes(Integer intSeq);

	/**
	 * Obtem a observacaoCenso de uma internacao com data de criacao menor ou
	 * igual a data passada como parametro e também a partir do id da
	 * internacao.
	 * 
	 * @param intSeq
	 * @return AinObservacoesCenso
	 */
	
	public AinObservacoesCenso obterObservacaoDaInternacao(Integer intSeq,
			Date data);

	/**
	 * Inclui ou edita uma observacao.
	 * 
	 * @param observacaoCenso
	 * @throws ApplicationBusinessException
	 */
	
	public void persistirObservacao(AinObservacoesCenso observacaoCenso)
			throws ApplicationBusinessException;

	/**
	 * 
	 * @param leito
	 * @return
	 */
	
	public AinExtratoLeitos obterUltimoExtratoLeito(String leito);

	public void validaDadosPesquisaReferencialEspecialidadeProfissional(
			AghEspecialidades especialidade) throws ApplicationBusinessException;

	public Long pesquisarReferencialEspecialidadeProfissonalGridVOCount(
			AghEspecialidades especialidade) throws ApplicationBusinessException;

	/**
	 * Conversão da pesquisa sobre a view V_AIN_PES_REF_ESP_PRO, utilizada na
	 * funcionalidade "Pesquisar referencial Especialidade/Profissional".
	 * 
	 * @return
	 */
	
	public List<ReferencialEspecialidadeProfissonalGridVO> pesquisarReferencialEspecialidadeProfissonalGridVO(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghEspecialidades especialidade)
			throws ApplicationBusinessException;

	public Boolean mostrarEstadoSaude(Short seqUnidadeFuncional);

	boolean verificarExisteInternacao(Integer numeroProntuario)
			throws ApplicationBusinessException;
	
	/**
	 * Função pacienteNotifGMR Verifica se o paciente possui notificação de
	 * germe multirresistente. Chamar função: MCIC_PAC_NOTIF_GMR
	 * 
	 * @param Integer aipPacientesCodigo
	 * @param String sLogin
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Boolean pacienteNotifGMR(Integer aipPacientesCodigo);
	
	AinInternacao obterInternacaoPacientePorCodPac(Integer codigoPaciente);

	public AinInternacao obterInternacaoPacienteInternado(Integer codigoPaciente);

	Short aincRetUnfSeq(AghUnidadesFuncionais unidadeFuncional,
			AinQuartos quarto, AinLeitos leito);
	
	public void excluirObservacaoDaInternacao(AinObservacoesCenso observacaoCenso);
	
	public List<VAinCensoVO> pesquisarCensoDiarioPacientesSemPaginator(Short unfSeq, Short unfSeqMae, Date data, 
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException;

	public AghUnidadesFuncionais obterUnidadeFuncional(Short unfSeq, Short unfSeqPai);

	Integer pesquisarCensoDiarioPacientesCount(Short unfSeq, Short unfSeqMae,
			Date data, DominioSituacaoUnidadeFuncional status)
			throws ApplicationBusinessException;
}