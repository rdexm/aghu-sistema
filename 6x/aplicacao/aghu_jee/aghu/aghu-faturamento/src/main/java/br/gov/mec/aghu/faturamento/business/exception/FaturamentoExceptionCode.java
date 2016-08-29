package br.gov.mec.aghu.faturamento.business.exception;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Auto generated
 * @author Gustavo Kuhn Andriotti
 * Created on: 2011.03.03-17:01:16
 */
@SuppressWarnings("ucd")
public enum FaturamentoExceptionCode implements BusinessExceptionCode {

	/**
	 * Capacidade deve ser maior que o Utilizado. Capacidade = {1}. Utilizado = {2}
	 */
	FAT_00000,
	/**
	 * Convenio Saude com este Código já cadastrado.
	 */
	FAT_00001,
	/**
	 * Conta Hospitalar com este Codigo já cadastrado.
	 */
	FAT_00002,
	/**
	 * Não existe Convenio Saude com este código.
	 */
	FAT_00003,
	/**
	 * Não existe Servidor
	 */
	FAT_00004,
	/**
	 * Não existe Servidor
	 */
	FAT_00005,
	/**
	 * Não existe Proced Hospitalar Interno com este Seq.
	 */
	FAT_00006,
	/**
	 * Não existe Cid com este Seq.
	 */
	FAT_00007,
	/**
	 * Não existe Cid com este Seq.
	 */
	FAT_00008,
	/**
	 * Não existe Situacao Alta com este Seq.
	 */
	FAT_00009,
	/**
	 * Não existe Tipo Alta Convenio com este Código, Seq.
	 */
	FAT_00010,
	/**
	 * Não existe Proced Hospitalar Interno com este Seq.
	 */
	FAT_00011,
	/**
	 * Não existe Conta Hospitalar com este Codigo.
	 */
	FAT_00012,
	/**
	 * Não existe Servidor
	 */
	FAT_00013,
	/**
	 * Conv Grupo Item Proced com este Seq, Código, Seq, Seq, Seq já cadastrado.
	 */
	FAT_00014,
	/**
	 * Não existe Convenio Proced Grupo com este Seq, Código, Seq.
	 */
	FAT_00015,
	/**
	 * Não existe Item da Tabela com este código
	 */
	FAT_00016,
	/**
	 * Não existe procedimento hospitalar interno com este código
	 */
	FAT_00017,
	/**
	 * Convenio Proced Grupo com este Seq, Código, Seq já cadastrado.
	 */
	FAT_00018,
	/**
	 * Não existe Convenio Proced Hospitalar com este Seq, Código.
	 */
	FAT_00019,
	/**
	 * Tipo de Grupo Conta informado não foi encontrado
	 */
	FAT_00020,
	/**
	 * Convenio Proced Hospitalar com este Seq, Código já cadastrado.
	 */
	FAT_00021,
	/**
	 * Tipo de Tabela informado não encontrado
	 */
	FAT_00022,
	/**
	 * Não existe Convênio Saude com este código
	 */
	FAT_00023,
	/**
	 * Tipo de documento já cadastrado para este convênio/plano
	 */
	FAT_00024,
	/**
	 * Não existe Convenio Saude com este Código.
	 */
	FAT_00025,
	/**
	 * Não existe tipo de documento com este código
	 */
	FAT_00026,
	/**
	 * Documento Internacao Pendente com este Código, Seq já cadastrado.
	 */
	FAT_00027,
	/**
	 * Não existe Convenio Tipo Documento com este Código, Seq.
	 */
	FAT_00028,
	/**
	 * Grupo Conta com este Seq já cadastrado.
	 */
	FAT_00029,
	/**
	 * Grupo Proced Hospitalar com este Seq já cadastrado.
	 */
	FAT_00030,
	/**
	 * Não existe tipo de agrupamento com este código
	 */
	FAT_00031,
	/**
	 * Não existe Grupo Proced Hospitalar com este Seq.
	 */
	FAT_00032,
	/**
	 * Item Conta Hospitalar com este Codigo, Seq já cadastrado.
	 */
	FAT_00033,
	/**
	 * Não existe Conta Hospitalar com este Codigo.
	 */
	FAT_00034,
	/**
	 * Não existe Proced Hospitalar Interno com este Seq.
	 */
	FAT_00035,
	/**
	 * Fat_Adiantamentos_Conta_Hosp com este Seq já cadastrado.
	 */
	FAT_00036,
	/**
	 * Fat_Adiantamentos_Conta_Hosp com este Codigo, Ich Seq já cadastrado.
	 */
	FAT_00037,
	/**
	 * Não existe banco/agência com estes códigos
	 */
	FAT_00038,
	/**
	 * Não existe conta hospitalar com este código
	 */
	FAT_00039,
	/**
	 * Não existe procedimento hospitalar interno com este código
	 */
	FAT_00040,
	/**
	 * Grupo já cadastrado para este procedimento hospitalar interno
	 */
	FAT_00041,
	/**
	 * Não existe Item Proced Hospitalar com este Seq, Seq.
	 */
	FAT_00042,
	/**
	 * Não existe grupo de procedimento hospitalar com este código
	 */
	FAT_00043,
	/**
	 * Item Proced Hospitalar com este Seq, Seq já cadastrado.
	 */
	FAT_00044,
	/**
	 * Não existe Procedimento Hospitalar com este Seq.
	 */
	FAT_00045,
	/**
	 * Não existe clínica com este código
	 */
	FAT_00046,
	/**
	 * Cid já está relacionado com este procedimento hospitalar interno
	 */
	FAT_00047,
	/**
	 * Não existe procedimento hospitalar interno com este código
	 */
	FAT_00048,
	/**
	 * Não existe Cid com este código
	 */
	FAT_00049,
	/**
	 * Proced Hospitalar Interno com este código já cadastrado
	 */
	FAT_00050,
	/**
	 * Procedimento Hospitalar com este código já cadastrado
	 */
	FAT_00051,
	/**
	 * Situação da alta com este código já cadastrado
	 */
	FAT_00052,
	/**
	 * Tipo de Agrupamento com este código já cadastrado
	 */
	FAT_00053,
	/**
	 * Tipo Alta Convênio com este Código, Seq já cadastrado.
	 */
	FAT_00054,
	/**
	 * Não existe convênio saude com este código
	 */
	FAT_00055,
	/**
	 * Tipo Documento com este Seq já cadastrado.
	 */
	FAT_00056,
	/**
	 * Conta Atend Urgencia com este Codigo, Seq, Seq já cadastrado.
	 */
	FAT_00057,
	/**
	 * Não existe Conta Hospitalar com este Codigo.
	 */
	FAT_00058,
	/**
	 * Não existe Atendimento Urgencia com este Seq.
	 */
	FAT_00059,
	/**
	 * Conta Hospital Dia com este Codigo já cadastrado.
	 */
	FAT_00060,
	/**
	 * Não existe Conta Hospitalar com este Codigo.
	 */
	FAT_00061,
	/**
	 * Conta Internacao com este Codigo, Seq, Codigo já cadastrado.
	 */
	FAT_00062,
	/**
	 * Não existe Internacao
	 */
	FAT_00063,
	/**
	 * Não existe Conta Hospitalar com este Codigo.
	 */
	FAT_00064,
	/**
	 * Não existe Hospital Dia com este Codigo, Seq.
	 */
	FAT_00065,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00066,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00067,
	/**
	 * Fat_Itens_Conta_Hospitalar com este Codigo, Seq já cadastrado.
	 */
	FAT_00068,
	/**
	 * Não existe Conta Hospitalar com este Codigo.
	 */
	FAT_00069,
	/**
	 * Não existe Proced Hospitalar Interno com este Seq.
	 */
	FAT_00070,
	/**
	 * Não existe Banco/Agência com estes códigos
	 */
	FAT_00071,
	/**
	 * Data da alta administrativa deve ser maior ou igual a data da internação administrativa.
	 */
	FAT_00072,
	/**
	 * Item da Tabela já cadastrado para este Grupo, Tabela, Convênio/Plano
	 */
	FAT_00073,
	/**
	 * Não existe Conv Proced Grupo com este Código, Seq, Código, Grupo.
	 */
	FAT_00074,
	/**
	 * Conv Proced Grupo com este Código, Seq, Código, Grupo já cadastrado.
	 */
	FAT_00075,
	/**
	 * Não existe Conv Proced Hospitalar com este Código, Seq, Código.
	 */
	FAT_00076,
	/**
	 * Não existe convênio saude/plano com estes códigos
	 */
	FAT_00077,
	/**
	 * Não existe convênio saude/plano com estes códigos
	 */
	FAT_00078,
	/**
	 * Acomodação já cadastrada para este convênio/plano
	 */
	FAT_00079,
	/**
	 * Não existe acomodação com este código
	 */
	FAT_00080,
	/**
	 * Não existe convênio saude/plano com estes códigos
	 */
	FAT_00081,
	/**
	 * Plano com este código já cadastrado
	 */
	FAT_00082,
	/**
	 * Não existe convênio saude com este código
	 */
	FAT_00083,
	/**
	 * Valor não existe no domínio Ich_Type
	 */
	FAT_00084,
	/**
	 * Convênio Saude com esta descrição já cadastrado
	 */
	FAT_00085,
	/**
	 * Tipo de Documento com esta descrição já cadastrado
	 */
	FAT_00086,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00087,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00088,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00089,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00090,
	/**
	 * Valor não existe no domínio Sexo_Ambos
	 */
	FAT_00091,
	/**
	 * Idade mínima deve ser menor ou igual a idade máxima
	 */
	FAT_00092,
	/**
	 * Escolha o desconto ou o acréscimo ou deixe-os em branco
	 */
	FAT_00093,
	/**
	 * Ao escolher o desconto automático informe o valor do desconto
	 */
	FAT_00094,
	/**
	 * O percentual de desconto não pode ser negativo
	 */
	FAT_00095,
	/**
	 * O percentual de acréscimo não pode ser negativo
	 */
	FAT_00096,
	/**
	 * Não há inclusões à atualizar
	 */
	FAT_00097,
	/**
	 * Operação cancelada
	 */
	FAT_00098,
	/**
	 * Não existe Convenio Saude com este código
	 */
	FAT_00099,
	/**
	 * Não existe convênio saude/plano com estes códigos
	 */
	FAT_00100,
	/**
	 * Não existe Procedimento Hospitalar com este código
	 */
	FAT_00101,
	/**
	 * Não existe tipo de alta médica com este código
	 */
	FAT_00102,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00103,
	/**
	 * Não existe convênio saude/plano com estes códigos
	 */
	FAT_00104,
	/**
	 * Não existe acomodação com este código
	 */
	FAT_00105,
	/**
	 * Cid Conta Hospitalar com este Código, Código já cadastrado.
	 */
	FAT_00106,
	/**
	 * Não existe Conta Hospitalar com este Código.
	 */
	FAT_00107,
	/**
	 * Não existe Cid com este Código.
	 */
	FAT_00108,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00109,
	/**
	 * Valor não existe no domínio Prioridade_Cid
	 */
	FAT_00110,
	/**
	 * Erro no acesso ao parâmetro P_COD_PHI_ADIANT_CONTA. Contate GSIS
	 */
	FAT_00111,
	/**
	 * Não existe Banco/Agência com estes códigos
	 */
	FAT_00112,
	/**
	 * Não existe Fat_Itens_Conta_Hospitalar com este Conta.
	 */
	FAT_00113,
	/**
	 * V_Fat_Procedimentos com este Código, Seq, Código, Grupo, Tipo tabela, Procedimento já cadastrado.
	 */
	FAT_00114,
	/**
	 * V_Fat_Conta_Hosp_Internacao com este Conta, Internação já cadastrado.
	 */
	FAT_00115,
	/**
	 * Não existe Internacao
	 */
	FAT_00116,
	/**
	 * Não existe Acomodacao com este Código.
	 */
	FAT_00117,
	/**
	 * Informe o número do recibo do pagamento de adiantamento
	 */
	FAT_00118,
	/**
	 * Preencha Banco/Agência, Número da Conta, Cheque e Série ou não informe nada
	 */
	FAT_00119,
	/**
	 * Se informado CPF, preencha também o nome do responsável pelo adiantamento
	 */
	FAT_00120,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00121,
	/**
	 * Valor não existe no domínio Grupo Convenio
	 */
	FAT_00122,
	/**
	 * Verifica escala só pode assinalado para convênio que permite internação
	 */
	FAT_00123,
	/**
	 * Idade mínima do paciente para execução do procedimento hospitalar deve ser maior ou igual a zero
	 */
	FAT_00124,
	/**
	 * Idade máxima do paciente para execução do procedimento hospitalar deve ser maior ou igual a zero
	 */
	FAT_00125,
	/**
	 * Não existe Motivo Saida Paciente com este Seq.
	 */
	FAT_00126,
	/**
	 * Motivo Saida Paciente com este Seq já cadastrado.
	 */
	FAT_00127,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00128,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00129,
	/**
	 * Não existe Situacao Saida Paciente com este Seq, Código.
	 */
	FAT_00130,
	/**
	 * Já existe um item desta tabela com o mesmo código. Verifique.
	 */
	FAT_00131,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00132,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00133,
	/**
	 * Não existe Fat_Itens_Conta_Hospitalar com este Conta.
	 */
	FAT_00134,
	/**
	 * V_Fat_Procedimentos com este Código Tabela já cadastrado.
	 */
	FAT_00135,
	/**
	 * Não existe Clinica com este Clínica.
	 */
	FAT_00136,
	/**
	 * Não existe Hospital Dia com este Atendimento Hosp Dia.
	 */
	FAT_00137,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00138,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00139,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00140,
	/**
	 * Não existe Material com este Material.
	 */
	FAT_00141,
	/**
	 * Não existe Procedimento Cirurgico com este Seq.
	 */
	FAT_00142,
	/**
	 * Não existe Proced Especial Diverso com este Seq.
	 */
	FAT_00143,
	/**
	 * Não existe Componente Sanguineo
	 */
	FAT_00144,
	/**
	 * Não existe Procedimento Hemoterapico com este Código.
	 */
	FAT_00145,
	/**
	 * Não existe Servidor
	 */
	FAT_00146,
	/**
	 * Apenas um entre Conv Saude Plano pode ser especificado
	 */
	FAT_00147,
	/**
	 * Apenas um entre Material, Procedimento Cirurgico, Proced Especial Diverso, Componente Sanguineo
	 */
	FAT_00148,
	/**
	 * Erro na recuperação do parâmetro P_DIAS_PERM_DEL_FAT na FATK_RN.RN_FATP_VER_DEL
	 */
	FAT_00150,
	/**
	 * Não é possível excluir o registro por estar fora do período permitido para exclusão
	 */
	FAT_00151,
	/**
	 * Erro na exclusão de procedimentos hospitalares internos
	 */
	FAT_00154,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00155,
	/**
	 * Não existe Exame Material Analise
	 */
	FAT_00156,
	/**
	 * Não existe Servidor com esta Matrícula, Vínculo.
	 */
	FAT_00157,
	/**
	 * Não existe Item Solicitacao Exame
	 */
	FAT_00158,
	/**
	 * Valor não existe no domínio Situacao Item Conta Hosp
	 */
	FAT_00159,
	/**
	 * Não existe Tipo Alta Convenio com este Convênio, Tipo alta convênio.
	 */
	FAT_00160,
	/**
	 * Conta de Atendimento com esta Seqüência e Código já cadastrada
	 */
	FAT_00161,
	/**
	 * Não existe Atendimento com esta Seqüência
	 */
	FAT_00162,
	/**
	 * Não existe Conta Hospitalar com este Código. Confirme horário de utilização do material e horário de internação do paciente.
	 */
	FAT_00163,
	/**
	 * Competência Procedimento Hospitalar já cadatsrada
	 */
	FAT_00164,
	/**
	 * Não existe Procedimento Hospitalar com este Código.
	 */
	FAT_00165,
	/**
	 * Vlr Item Proced Hosp Comp com este Proced Hospitalar, Seqp, Tipo tabela, Procedimento já cadastrado.
	 */
	FAT_00166,
	/**
	 * Não existe Competencia Proced Hospitalar com este Proced Hospitalar, Seqp.
	 */
	FAT_00167,
	/**
	 * Não existe Item Proced Hospitalar com este Tipo tabela, Procedimento.
	 */
	FAT_00168,
	/**
	 * Vlr Item Proced Hosp Comp com este Código, Dt Inicio, Procedimento, Tipo tabela já cadastrado.
	 */
	FAT_00169,
	/**
	 * Não existe Competencia Proced Hospitalar com este Código, Dt Inicio.
	 */
	FAT_00170,
	/**
	 * Não existe Item Proced Hospitalar com este Tipo tabela, Procedimento.
	 */
	FAT_00171,
	/**
	 * Somente uma competência pode ter a Data Fim nula
	 */
	FAT_00172,
	/**
	 * Data Final deve ser > ou = a Data Inicio
	 */
	FAT_00173,
	/**
	 * O intervalo de datas informado está incorreto
	 */
	FAT_00174,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00175,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00176,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00177,
	/**
	 * Não existe Clinica com este Clínica.
	 */
	FAT_00178,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00179,
	/**
	 * Não existe Clinica com este Clc_Codigo.
	 */
	FAT_00180,
	/**
	 * V_Fat_Ssm_Internacao com este Cod_Tabela já cadastrado.
	 */
	FAT_00181,
	/**
	 * Aih com este Nro Aih já cadastrado.
	 */
	FAT_00182,
	/**
	 * Não existe Servidor
	 */
	FAT_00183,
	/**
	 * Apac Area Irradiada com este APAC, Seq, Seq já cadastrado.
	 */
	FAT_00184,
	/**
	 * Não existe Fat_Apac_Area_Irradiadas com este APAC, Seq.
	 */
	FAT_00185,
	/**
	 * Não existe Cid com este Código.
	 */
	FAT_00186,
	/**
	 * Ato Medico Aih com este Seq, Código, Seq já cadastrado.
	 */
	FAT_00187,
	/**
	 * Não existe Espelho Aih com este Código, Seq.
	 */
	FAT_00188,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00189,
	/**
	 * Campo Medico Audit Aih com este Seq, Código, Seq já cadastrado.
	 */
	FAT_00190,
	/**
	 * Não existe Espelho Aih com este Código, Seq.
	 */
	FAT_00191,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00192,
	/**
	 * Competencia com este Modulo, Mes, Ano, Dt Hr Inicio já cadastrado.
	 */
	FAT_00193,
	/**
	 * Fat_Conta_Apacs com este APAC, Seq já cadastrado.
	 */
	FAT_00194,
	/**
	 * Não existe Atendimento Apac com este APAC.
	 */
	FAT_00195,
	/**
	 * Não existe Competencia com este Modulo, Mes, Ano, Dt Hr Inicio.
	 */
	FAT_00196,
	/**
	 * Não existe Motivo Cobranca Apac com este Codigo.
	 */
	FAT_00197,
	/**
	 * Não existe Paciente com este Codigo.
	 */
	FAT_00198,
	/**
	 * Não existe Cid com este Código.
	 */
	FAT_00199,
	/**
	 * Não existe Cid com este Código.
	 */
	FAT_00200,
	/**
	 * Não existe Servidor
	 */
	FAT_00201,
	/**
	 * Não existe Fat_Conta_Apacs com este APAC, Seq.
	 */
	FAT_00202,
	/**
	 * Não existe Fat_Conta_Apacs com este APAC, Seq.
	 */
	FAT_00203,
	/**
	 * Não existe Estadio Tumor com este Codigo.
	 */
	FAT_00204,
	/**
	 * Não existe Finalidade Trat com este Codigo.
	 */
	FAT_00205,
	/**
	 * Conta Atend com este Seq já cadastrado.
	 */
	FAT_00206,
	/**
	 * Não existe Atendimento
	 */
	FAT_00207,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_00208,
	/**
	 * Conta Sugestao Desdobr com este Codigo, Código, Dthr Sugestao já cadastrado.
	 */
	FAT_00209,
	/**
	 * Não existe Motivo Desdobramento com este Codigo.
	 */
	FAT_00210,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00211,
	/**
	 * Conv Atividade Prof com este Código, Codigo já cadastrado.
	 */
	FAT_00212,
	/**
	 * Não existe Convenio Saude com este Código.
	 */
	FAT_00213,
	/**
	 * Conv Atv Prof Esp com este Codigo, Código, Código já cadastrado.
	 */
	FAT_00214,
	/**
	 * Não existe Conv Atividade Prof com este Código, Codigo.
	 */
	FAT_00215,
	/**
	 * Não existe Especialidade
	 */
	FAT_00216,
	/**
	 * Conv Faixa Etaria com este Código, Codigo já cadastrado.
	 */
	FAT_00217,
	/**
	 * Não existe Convenio Saude com este Código.
	 */
	FAT_00218,
	/**
	 * Conv Grupo Atendimento com este Código, Codigo já cadastrado.
	 */
	FAT_00219,
	/**
	 * Conv Grupo Atendimento com este Código, Código já cadastrado.
	 */
	FAT_00220,
	/**
	 * Não existe Convenio Saude com este Código.
	 */
	FAT_00221,
	/**
	 * Não existe Especialidade
	 */
	FAT_00222,
	/**
	 * Conv Item Proc Ativ Prof com este Procedimento, Tipo tabela, Codigo, Código já cadastrado.
	 */
	FAT_00223,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00224,
	/**
	 * Não existe Conv Atividade Prof com este Código, Codigo.
	 */
	FAT_00225,
	/**
	 * Conv Tipo Consulta com este Código, Codigo já cadastrado.
	 */
	FAT_00226,
	/**
	 * Conv Tipo Consulta com este Código, Tipo já cadastrado.
	 */
	FAT_00227,
	/**
	 * Não existe Convenio Saude com este Código.
	 */
	FAT_00228,
	/**
	 * Não existe Tipo Consulta com este Tipo.
	 */
	FAT_00229,
	/**
	 * Documento Cobranca Aih com este Codigo Dcih já cadastrado.
	 */
	FAT_00230,
	/**
	 * Não existe Competencia com este Modulo, Mes, Ano, Dt Hr Inicio.
	 */
	FAT_00231,
	/**
	 * Não existe Clinica com este Código.
	 */
	FAT_00232,
	/**
	 * Espelho Aih com este Código, Seq já cadastrado.
	 */
	FAT_00233,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00234,
	/**
	 * Não existe Tipo Aih com este Codigo.
	 */
	FAT_00235,
	/**
	 * Estadio Tumor com este Codigo já cadastrado.
	 */
	FAT_00236,
	/**
	 * Exc Carater Internacao com este Sigla, Procedimento, Tipo tabela já cadastrado.
	 */
	FAT_00237,
	/**
	 * Não existe Uf com este Sigla.
	 */
	FAT_00238,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00239,
	/**
	 * Não existe Tipo Carater Internacao com este Seq.
	 */
	FAT_00240,
	/**
	 * Finalidade Trat com este Codigo já cadastrado.
	 */
	FAT_00241,
	/**
	 * Item Conta Apac
	 */
	FAT_00242,
	/**
	 * Não existe Fat_Itens_Conta_Apac com este APAC, Seq.
	 */
	FAT_00243,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00244,
	/**
	 * Não existe Servidor
	 */
	FAT_00245,
	/**
	 * Não existe Item Solicitacao Exame com este Solicitação, Item.
	 */
	FAT_00246,
	/**
	 * Não existe Consulta Proced Hospitalar com este Consulta, Procedimento.
	 */
	FAT_00247,
	/**
	 * Item Cta Atend com este Seq, Seqp já cadastrado.
	 */
	FAT_00248,
	/**
	 * Não existe Conta Atend com este Seq.
	 */
	FAT_00249,
	/**
	 * Não existe Unidade Funcional
	 */
	FAT_00250,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00251,
	/**
	 * Item Proc Hosp Compat com este Procedimento, Tipo tabela, Procedimento, Tipo tabela já cadastrado.
	 */
	FAT_00252,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00253,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00254,
	/**
	 * Item Proc Hosp Exclusi com este Procedimento, Tipo tabela, Procedimento, Tipo tabela já cadastrado.
	 */
	FAT_00255,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00256,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00257,
	/**
	 * Local Aplicacao Regra com este Nome, Seqp já cadastrado.
	 */
	FAT_00258,
	/**
	 * Não existe Regra Negocio
	 */
	FAT_00259,
	/**
	 * Motivo Cobranca Apac com este Codigo já cadastrado.
	 */
	FAT_00260,
	/**
	 * Motivo Desdobramento com este Codigo já cadastrado.
	 */
	FAT_00261,
	/**
	 * Não existe Tipo Aih com este Codigo.
	 */
	FAT_00262,
	/**
	 * Paciente Transplante com este Codigo, Codigo, Dt Inscricao Transpante já cadastrado.
	 */
	FAT_00263,
	/**
	 * Não existe Paciente com este Codigo.
	 */
	FAT_00264,
	/**
	 * Não existe Tipo Transplante com este Codigo.
	 */
	FAT_00265,
	/**
	 * Paciente Tratamento com este Codigo, Dt Diagnostico Tumor já cadastrado.
	 */
	FAT_00266,
	/**
	 * Não existe Paciente com este Codigo.
	 */
	FAT_00267,
	/**
	 * Parametro Geral com este Nome Parametro já cadastrado.
	 */
	FAT_00268,
	/**
	 * Proced Amb Realizado com este Seq já cadastrado.
	 */
	FAT_00269,
	/**
	 * Não existe Competencia com este Modulo, Mes, Ano, Dt Hr Inicio.
	 */
	FAT_00270,
	/**
	 * Não existe Item Solicitacao Exame com este Solicitação, Item.
	 */
	FAT_00271,
	/**
	 * Não existe Consulta Proced Hospitalar com este Consulta, Procedimento.
	 */
	FAT_00272,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00273,
	/**
	 * Não existe Servidor
	 */
	FAT_00274,
	/**
	 * Não existe Unidade Funcional
	 */
	FAT_00275,
	/**
	 * Não existe Especialidade
	 */
	FAT_00276,
	/**
	 * Não existe Paciente com este Codigo.
	 */
	FAT_00277,
	/**
	 * Regra Negocio
	 */
	FAT_00278,
	/**
	 * Não existe Tipo Regra com este Codigo.
	 */
	FAT_00279,
	/**
	 * Não existe Sistema com este Sigla.
	 */
	FAT_00280,
	/**
	 * Tipo Aih com este Codigo já cadastrado.
	 */
	FAT_00281,
	/**
	 * Tipo Ato com este Codigo já cadastrado.
	 */
	FAT_00282,
	/**
	 * Tipo Ato Proced com este Codigo, Procedimento, Tipo tabela já cadastrado.
	 */
	FAT_00283,
	/**
	 * Não existe Tipo Ato com este Codigo.
	 */
	FAT_00284,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00285,
	/**
	 * Tipo Classif Sec Saude com este Codigo já cadastrado.
	 */
	FAT_00286,
	/**
	 * Tipo Regra com este Codigo já cadastrado.
	 */
	FAT_00287,
	/**
	 * Tipo Transplante com este Codigo já cadastrado.
	 */
	FAT_00288,
	/**
	 * Tipo Vinculo com este Codigo já cadastrado.
	 */
	FAT_00289,
	/**
	 * Apenas um entre Material, Procedimento Cirurgico, Proced Especial Diverso, Componente Sanguineo
	 */
	FAT_00290,
	/**
	 * Apenas um entre Item Solicitacao Exame, Consulta Proced Hospitalar pode ser especificado
	 */
	FAT_00291,
	/**
	 * Apenas um entre Item Solicitacao Exame, Consulta Proced Hospitalar pode ser especificado
	 */
	FAT_00292,
	/**
	 * Valor não existe no domínio Situacao Aih
	 */
	FAT_00293,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00294,
	/**
	 * Valor não existe no domínio Modulo Competencia
	 */
	FAT_00295,
	/**
	 * Valor não existe no domínio Cap_Type
	 */
	FAT_00296,
	/**
	 * Valor não existe no domínio Tipo Apac
	 */
	FAT_00297,
	/**
	 * Valor não existe no domínio Situacao Conta
	 */
	FAT_00298,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00299,
	/**
	 * Valor não existe no domínio Positivo_Negativo
	 */
	FAT_00300,
	/**
	 * Valor não existe no domínio Positivo_Negativo
	 */
	FAT_00301,
	/**
	 * Valor não existe no domínio Positivo_Negativo
	 */
	FAT_00302,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00303,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00304,
	/**
	 * Valor não existe no domínio Modulo Competencia
	 */
	FAT_00305,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00306,
	/**
	 * Valor não existe no domínio Local Cobranca
	 */
	FAT_00307,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00308,
	/**
	 * Valor não existe no domínio Tipo Dcih
	 */
	FAT_00309,
	/**
	 * Valor não existe no domínio Modulo Competencia
	 */
	FAT_00310,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00311,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00312,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00313,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00314,
	/**
	 * Valor não existe no domínio Tipo_Tratamento
	 */
	FAT_00315,
	/**
	 * Valor não existe no domínio Situacao Item Conta
	 */
	FAT_00316,
	/**
	 * Valor não existe no domínio Local Cobranca
	 */
	FAT_00317,
	/**
	 * Valor não existe no domínio Modulo Competencia
	 */
	FAT_00318,
	/**
	 * Não existe Situacao Saida Paciente com este Seq, Código.
	 */
	FAT_00319,
	/**
	 * Não existe Tipo Alta Convenio com este Convênio, Tipo alta convênio.
	 */
	FAT_00320,
	/**
	 * Não existe Documento Cobranca Aih com este Codigo Dcih.
	 */
	FAT_00321,
	/**
	 * Não existe Motivo Desdobramento com este Codigo.
	 */
	FAT_00322,
	/**
	 * Não existe Tipo Classif Sec Saude com este Codigo.
	 */
	FAT_00323,
	/**
	 * Não existe Tipo Aih com este Codigo.
	 */
	FAT_00324,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00325,
	/**
	 * Não existe Aih com este Nro Aih.
	 */
	FAT_00326,
	/**
	 * Não existe Tipo Vinculo com este Codigo.
	 */
	FAT_00327,
	/**
	 * Não existe Unidade Funcional
	 */
	FAT_00328,
	/**
	 * Ato Medico Aih com este Seq, Código, Seq já cadastrado.
	 */
	FAT_00329,
	/**
	 * Não existe Espelho Aih com este Código, Seq.
	 */
	FAT_00330,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00331,
	/**
	 * Não existe Tipo Ato com este Codigo.
	 */
	FAT_00332,
	/**
	 * Não existe Tipo Vinculo com este Codigo.
	 */
	FAT_00333,
	/**
	 * Campo Medico Audit Aih com este Seq, Código, Seq já cadastrado.
	 */
	FAT_00334,
	/**
	 * Não existe Espelho Aih com este Código, Seq.
	 */
	FAT_00335,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00336,
	/**
	 * Ato Obrigatorio Proced com este Tipo tabela, Procedimento, Tipo tabela, Procedimento, Codigo, Codigo já cadastrado.
	 */
	FAT_00337,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00338,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00339,
	/**
	 * Não existe Tipo Vinculo com este Codigo.
	 */
	FAT_00340,
	/**
	 * Não existe Tipo Ato com este Codigo.
	 */
	FAT_00341,
	/**
	 * Diaria Internacao com este Seq já cadastrado.
	 */
	FAT_00342,
	/**
	 * Excecao Percentual com este Tipo tabela, Procedimento, Seq já cadastrado.
	 */
	FAT_00343,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00344,
	/**
	 * Valor Diaria Internacao com este Dt Hr Inicio, Modulo, Mes, Ano, Seq já cadastrado.
	 */
	FAT_00345,
	/**
	 * Não existe Competencia com este Modulo, Mes, Ano, Dt Hr Inicio.
	 */
	FAT_00346,
	/**
	 * Não existe Diaria Internacao com este Seq.
	 */
	FAT_00347,
	/**
	 * Valor não existe no domínio Exclusao_Critica
	 */
	FAT_00348,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00349,
	/**
	 * Valor não existe no domínio Situacao Conta
	 */
	FAT_00350,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00351,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00352,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00353,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00354,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00355,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00356,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00357,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00358,
	/**
	 * Valor não existe no domínio Origem Itm Fat Int
	 */
	FAT_00359,
	/**
	 * Valor não existe no domínio Local Cobranca
	 */
	FAT_00360,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00361,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00362,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00363,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00364,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00365,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00366,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00367,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00368,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00369,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00370,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00371,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00372,
	/**
	 * Valor não existe no domínio Modulo Competencia
	 */
	FAT_00373,
	/**
	 * Valor não existe no domínio Tipo_Quantidade
	 */
	FAT_00374,
	/**
	 * Compatibilidade já cadastrada.
	 */
	FAT_00375,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00376,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00377,
	/**
	 * Valor não existe no domínio Tipo_Compat_Exclus
	 */
	FAT_00378,
	/**
	 * Valor não existe no domínio Tipo_Comparacao
	 */
	FAT_00379,
	/**
	 * Valor não existe no domínio Tipo_Valor_Diaria
	 */
	FAT_00380,
	/**
	 * Não existe Tipo Ato com este Codigo.
	 */
	FAT_00381,
	/**
	 * Valor Diaria Internacao com este Seq, Data Inicio Validade já cadastrado.
	 */
	FAT_00382,
	/**
	 * Não existe Diaria Internacao com este Seq.
	 */
	FAT_00383,
	/**
	 * Valor não existe no domínio Indicador_Preenche_Cma
	 */
	FAT_00384,
	/**
	 * Valor não existe no domínio Utilizacao_Proc_Hosp
	 */
	FAT_00385,
	/**
	 * Conv Atv Prof Ufn com este Codigo, Código, Código já cadastrado.
	 */
	FAT_00386,
	/**
	 * Não existe Conv Atividade Prof com este Código, Codigo.
	 */
	FAT_00387,
	/**
	 * Não existe Unidade Funcional
	 */
	FAT_00388,
	/**
	 * Grupo Atendimento com este Código, Codigo já cadastrado.
	 */
	FAT_00389,
	/**
	 * Não existe Convenio Saude com este Código.
	 */
	FAT_00390,
	/**
	 * Grupo Atendimento Esp com este Código já cadastrado.
	 */
	FAT_00391,
	/**
	 * Não existe Especialidade
	 */
	FAT_00392,
	/**
	 * Não existe Grupo Atendimento com este Código, Codigo.
	 */
	FAT_00393,
	/**
	 * Motivo Desdobr Clinica com este Codigo, Código já cadastrado.
	 */
	FAT_00394,
	/**
	 * Não existe Motivo Desdobramento com este Codigo.
	 */
	FAT_00395,
	/**
	 * Não existe Clinica com este Código.
	 */
	FAT_00396,
	/**
	 * Item Grupo Atend com este Código, Codigo, Tipo tabela, Procedimento já cadastrado.
	 */
	FAT_00397,
	/**
	 * Não existe Grupo Atendimento com este Código, Codigo.
	 */
	FAT_00398,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00399,
	/**
	 * Valor não existe no domínio Ver_Motivo_Desdobr
	 */
	FAT_00400,
	/**
	 * Procedimento Hospitalar do Item deve ser igual ao Procedimento Hospitalar do Grupo
	 */
	FAT_00401,
	/**
	 * Limite de procedimentos no Campo Medico Auditor atingido
	 */
	FAT_00402,
	/**
	 * Data Hora de Início deve ser menor que a Data Hora de Fim.
	 */
	FAT_00403,
	/**
	 * Número máximo de Espelhos AIH ultrapassado.
	 */
	FAT_00404,
	/**
	 * Número máximo de Exceções Percentuais ultrapassado.
	 */
	FAT_00405,
	/**
	 * Limite de procedimentos nos Atos Medicos atingido: maximo {1}, encontrados {2}
	 */
	FAT_00406,
	/**
	 * Mês deve estar entre 1 e 12
	 */
	FAT_00407,
	/**
	 * Valor não existe no domínio Situacao_Competencia
	 */
	FAT_00408,
	/**
	 * Valor não existe no domínio Modo_Cobranca
	 */
	FAT_00409,
	/**
	 * Não existe Competencia com este Modulo, Mes, Ano, Dt Hr Inicio.
	 */
	FAT_00410,
	/**
	 * Valor não existe no domínio Tipo_Quantidade
	 */
	FAT_00411,
	/**
	 * Valor não existe no domínio Modulo Competencia
	 */
	FAT_00412,
	/**
	 * Valor não existe no domínio Tipo_Valor_Diaria
	 */
	FAT_00413,
	/**
	 * Valor não existe no domínio Situacao Item Apac
	 */
	FAT_00414,
	/**
	 * Procedimento especial necessita de todos os valores cadastrados em Vlr_Item_Proced_Hosp_Comp
	 */
	FAT_00415,
	/**
	 * Procedimento especial necessita de registro de valores sem data de fim de competência em Vlr_Item_Proced_Hosp_Comp
	 */
	FAT_00416,
	/**
	 * Item_Proced_Hospitalar pode ter somente um registro de valores sem data de fim de competência em Vlr_Item_Proced_Hosp_Comp
	 */
	FAT_00417,
	/**
	 * Usuário não cadastrado como servidor
	 */
	FAT_00418,
	/**
	 * Procedimento que exige valor necessita de registro de valores sem data de fim de competência em Vlr_Item_Proced_Hosp_Comp
	 */
	FAT_00419,
	/**
	 * Em conta hospitalar de convênio SUS somente pode ser cadastrado exatamente um CID primário e exatamente um CID secundario.
	 */
	FAT_00420,
	/**
	 * Não existe Fat_Contas_Hospitalares com este Código.
	 */
	FAT_00421,
	/**
	 * Não existe Fat_Itens_Conta_Hospitalar com este Conta.
	 */
	FAT_00422,
	/**
	 * Não existe Proced Hosp Interno com este Cth_Phi_Seq.
	 */
	FAT_00423,
	/**
	 * Não existe Proced Hosp Interno com este Cth_Phi_Seq_Realizado.
	 */
	FAT_00424,
	/**
	 * Não existe Conv Saude Plano com este Cth_Csp_Cnv_Codigo, Cth_Csp_Seq.
	 */
	FAT_00425,
	/**
	 * Não existe Convenio Saude com este Cth_Csp_Cnv_Codigo.
	 */
	FAT_00426,
	/**
	 * V_Fat_Contas_Hosp_Pacientes com este Cth_Seq já cadastrado.
	 */
	FAT_00427,
	/**
	 * Valor não existe no domínio Situacao Conta
	 */
	FAT_00428,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00429,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00430,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00431,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00432,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00433,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00434,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00435,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00436,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00437,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00438,
	/**
	 * Tipo de AIH do motivo de desdobramento deve ser igual ao tipo de AIH da conta hospitalar.
	 */
	FAT_00439,
	/**
	 * Tipo de AIH deve ser obrigatoriamente informado em Contas Hospitalares do convênio SUS.
	 */
	FAT_00440,
	/**
	 * Código SUS do tipo de AIH somente pode ser 1 ou 5 em Contas Hospitalares do convênio SUS.
	 */
	FAT_00441,
	/**
	 * Convênio do tipo da alta da conta hospitalar deve ser igual ao convênio do plano saude da conta hospitalar.
	 */
	FAT_00442,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00443,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00444,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00445,
	/**
	 * SSM da conta hospitalar não é um tipo de tratamento que gere AIH5; a conta hospitalar não pode possuir tipo de AIH "5".
	 */
	FAT_00446,
	/**
	 * SSM da conta hospitalar é um tipo de tratamento que gera AIH5, mas a primeira apresentação da conta hospitalar não pode possuir tipo de AIH 5, deve ser tipo 1.
	 */
	FAT_00447,
	/**
	 * SSM da conta hospitalar é um tipo de tratamento que gera AIH5; porém, somente a primeira apresentação da conta hospitalar deve possuir tipo de AIH 1, as demais apresentações devem ser tipo 5.
	 */
	FAT_00448,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_00449,
	/**
	 * Conv Faixa Etaria Item com este Codigo, Código, Tipo tabela, Procedimento já cadastrado.
	 */
	FAT_00450,
	/**
	 * Não existe Conv Faixa Etaria com este Código, Codigo.
	 */
	FAT_00451,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00452,
	/**
	 * Não exite Competência aberta para o Módulo
	 */
	FAT_00453,
	/**
	 * Conv Atv Prof Esp com este Código, Código já cadastrado.
	 */
	FAT_00454,
	/**
	 * Espec Grupo Atendimento com este Código, Código já cadastrado.
	 */
	FAT_00455,
	/**
	 * Valor não existe no domínio Local Cobranca
	 */
	FAT_00456,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00457,
	/**
	 * Não existe Fat_Conv_Grupo_Itens_Proced com este Código, Seq, Código, Grupo.
	 */
	FAT_00458,
	/**
	 * Não existe Fat_Itens_Conta_Apac com este APAC, Seq.
	 */
	FAT_00459,
	/**
	 * FAT_PMR_CK4
	 */
	FAT_00460,
	/**
	 * V_Fat_Conv_Plano_Grupo_Proceds com este Cph_Csp_Cnv_Codigo, Cph_Csp_Seq, Cph_Pho_Seq, Grc_Seq já cadastrado.
	 */
	FAT_00461,
	/**
	 * V_Fat_Contas_Apac_Pacientes com este Cap_Atm_Numero, Cap_Seqp já cadastrado.
	 */
	FAT_00462,
	/**
	 * Não existe Competencia com este Cap_Cpe_Ano, Cap_Cpe_Dt_Hr_Inicio, Cap_Cpe_Mes, Cap_Cpe_Modulo.
	 */
	FAT_00463,
	/**
	 * Não existe Cid com este Cap_Cid_Seq.
	 */
	FAT_00464,
	/**
	 * Não existe Cid com este Cap_Cid_Seq_Secundario.
	 */
	FAT_00465,
	/**
	 * Não existe Atendimento Apac com este Cap_Atm_Numero.
	 */
	FAT_00466,
	/**
	 * Procedimento Hospitalar Interno do Item deve ser igual ao Procedimento Hospitalar Interno da Consulta Proced Hospitalar associada.
	 */
	FAT_00467,
	/**
	 * Valor não existe no domínio Origem Proc Ambulatorio
	 */
	FAT_00468,
	/**
	 * Não existe Mat Ortese Prot Cirg com este Material, Seq.
	 */
	FAT_00469,
	/**
	 * Não existe Proc Esp Por Cirurgia
	 */
	FAT_00470,
	/**
	 * Não existe Proc Esp Por Cirurgia
	 */
	FAT_00471,
	/**
	 * Somente pode haver um registro ativo para o mesmo código SUS.
	 */
	FAT_00472,
	/**
	 * Valor não existe no domínio Tipo Transplante
	 */
	FAT_00473,
	/**
	 * Erro ao agrupar procedimentos do BPA.
	 */
	FAT_00474,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00475,
	/**
	 * Erro ao gerar APAC de continuação. Competência não encontrada.
	 */
	FAT_00476,
	/**
	 * Erro ao gerar APAC de continuação.
	 */
	FAT_00477,
	/**
	 * Não existe Proc Esp Por Cirurgia
	 */
	FAT_00478,
	/**
	 * Não existe Unidade Funcional
	 */
	FAT_00479,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00480,
	/**
	 * Não existe Proced Amb Realizado com este Pmr_Seq.
	 */
	FAT_00481,
	/**
	 * Não existe Unidade Funcional
	 */
	FAT_00482,
	/**
	 * Não existe Competencia com este Cpe_Ano, Cpe_Dt_Hr_Inicio, Cpe_Mes, Cpe_Modulo.
	 */
	FAT_00483,
	/**
	 * Não existe Espelho Conta Apac com este Cap_Atm_Numero, Cap_Seqp, Seqp_Continuacao.
	 */
	FAT_00484,
	/**
	 * Não existe Fat_Espelhos_Conta_Apac com este Cap_Atm_Numero, Cap_Seqp.
	 */
	FAT_00485,
	/**
	 * Espelho Conta Apac com este Cap_Atm_Numero, Cap_Seqp, Seqp_Continuacao já cadastrado.
	 */
	FAT_00486,
	/**
	 * Não existe Item Conta Apac
	 */
	FAT_00487,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00488,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00489,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00490,
	/**
	 * Não existe Fat_Arq_Espelhos_Itens_Apac com este Cap_Atm_Numero, Cap_Seqp.
	 */
	FAT_00491,
	/**
	 * Não existe Competencia com este Cpe_Ano, Cpe_Dt_Hr_Inicio, Cpe_Mes, Cpe_Modulo.
	 */
	FAT_00492,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00493,
	/**
	 * Não existe Competencia com este Cpe_Ano, Cpe_Dt_Hr_Inicio, Cpe_Mes, Cpe_Modulo.
	 */
	FAT_00494,
	/**
	 * Erro ao encerrar competência e criar nova competência aberta.
	 */
	FAT_00495,
	/**
	 * Item Conv Tipo Consulta com este Codigo, Código, Tipo tabela, Procedimento já cadastrado.
	 */
	FAT_00496,
	/**
	 * Não existe Conv Tipo Consulta com este Código, Codigo.
	 */
	FAT_00497,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00498,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00499,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00500,
	/**
	 * Este Procedimento Hospitalar exige que haja uma única associação entre seus Itens Proced Hospitalares e os Proced Hosp Internos.
	 */
	FAT_00501,
	/**
	 * Não existe Fat_Contas_Hospitalares com este .
	 */
	FAT_00502,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00503,
	/**
	 * Espelho Item Conta Hospitalar com este Código, Conta, Seqp já cadastrado.
	 */
	FAT_00504,
	/**
	 * Não existe Fat_Espelhos_Itens_Conta_Hosp com este Conta, Código.
	 */
	FAT_00505,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00506,
	/**
	 * Não existe Tipo Vinculo com este Codigo.
	 */
	FAT_00507,
	/**
	 * Não existe Tipo Ato com este Codigo.
	 */
	FAT_00508,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00509,
	/**
	 * Valor não existe no domínio Modo_Cobranca
	 */
	FAT_00510,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00511,
	/**
	 * Item já faturado. Não pode sofrer modificações.
	 */
	FAT_00512,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00513,
	/**
	 * Arq Espelho Proced Amb com este já cadastrado.
	 */
	FAT_00514,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00515,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00516,
	/**
	 * Não existe Cirurgia para faturamento
	 */
	FAT_00517,
	/**
	 * Item da Cirurgia sem correpondente em Procedimento Interno
	 */
	FAT_00518,
	/**
	 * Valor não existe no domínio Tipo_Item_Conta
	 */
	FAT_00520,
	/**
	 * Não existe conta hospitalar no período de realização da cirurgia
	 */
	FAT_00521,
	/**
	 * Não foi possível criar item de conta hospitalar para procedimento cirúrgico
	 */
	FAT_00522,
	/**
	 * Não existe Fat_Itens_Conta_Hospitalar com este Proc realizados.
	 */
	FAT_00523,
	/**
	 * Não existe Fat_Conv_Grupo_Itens_Proced com este Procedimento.
	 */
	FAT_00524,
	/**
	 * Não existe Fat_Aihs com esta Matrícula, Vínculo.
	 */
	FAT_00525,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00526,
	/**
	 * Não existe Competencia com este Cpe_Dt_Hr_Inicio.
	 */
	FAT_00527,
	/**
	 * Não existe Competencia com este Eca_Cpe_Ano, Eca_Cpe_Mes, Eca_Cpe_Modulo, Eca_Cpe_Dt_Hr_Inicio.
	 */
	FAT_00528,
	/**
	 * V_Fat_Contas_Hospitalar_Pac com este Cth_Seq já cadastrado.
	 */
	FAT_00529,
	/**
	 * Não existe Convenio Saude com este Cpg_Cph_Csp_Cnv_Codigo.
	 */
	FAT_00530,
	/**
	 * Não existe Conv Saude Plano com este Cpg_Cph_Csp_Cnv_Codigo, Cpg_Cph_Csp_Seq.
	 */
	FAT_00531,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00532,
	/**
	 * Não existe Proced Hosp Interno com este Phi_Seq.
	 */
	FAT_00533,
	/**
	 * V_Fat_Associacao_Procedimentos com este Cpg_Cph_Csp_Cnv_Codigo, Cpg_Cph_Csp_Seq, Iph_Pho_Seq, Iph_Seq, Phi_Seq já cadastrado.
	 */
	FAT_00534,
	/**
	 * Não existe Fat_Contas_Hospitalares com este Proc realizados.
	 */
	FAT_00535,
	/**
	 * Não existe Fat_Contas_Hospitalares com este Proc realizado.
	 */
	FAT_00536,
	/**
	 * Não existe Fat_Contas_Hospitalares com este .
	 */
	FAT_00537,
	/**
	 * V_Fat_Medicos_Auditores com este Matricula, Vin_Codigo já cadastrado.
	 */
	FAT_00538,
	/**
	 * Item Cta Atend com este Id já cadastrado.
	 */
	FAT_00539,
	/**
	 * Não existe Proc Esp Por Cirurgia
	 */
	FAT_00540,
	/**
	 * Não existe Consulta Proced Hospitalar com este Consulta, Procedimento.
	 */
	FAT_00541,
	/**
	 * Não existe Item Solicitacao Exame com este Solicitação, Item.
	 */
	FAT_00542,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00543,
	/**
	 * Não existe Convenio Saude com este .
	 */
	FAT_00544,
	/**
	 * Não existe Conv Saude Plano com este .
	 */
	FAT_00545,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00546,
	/**
	 * Não foi possível inserir Item Conta Atends
	 */
	FAT_00547,
	/**
	 * Não foi possível atualizar Item Conta Atends
	 */
	FAT_00548,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00549,
	/**
	 * Não existe Proced Amb Realizado com este Seq.
	 */
	FAT_00550,
	/**
	 * Não existe Fat_Log_Errors com este APAC, Seq.
	 */
	FAT_00551,
	/**
	 * Não existe Paciente com este Codigo.
	 */
	FAT_00552,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00553,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00554,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00555,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00556,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00557,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00558,
	/**
	 * Log Error com este já cadastrado.
	 */
	FAT_00559,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00560,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00561,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00562,
	/**
	 * Valor não existe no domínio Modulo Competencia
	 */
	FAT_00563,
	/**
	 * Não existe competência aberta para este módulo
	 */
	FAT_00564,
	/**
	 * Não existe Item Rmps com este Cum, RMR, Estoque.
	 */
	FAT_00565,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00566,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00567,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00568,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00569,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00570,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00571,
	/**
	 * Não existe Tipo Classif Sec Saude com este Codigo.
	 */
	FAT_00572,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00573,
	/**
	 * Valor não existe no domínio Situacao Conta
	 */
	FAT_00574,
	/**
	 * Valor não existe no domínio Tipo Uti
	 */
	FAT_00575,
	/**
	 * Valor não existe no domínio Tipo_Item_Conta
	 */
	FAT_00576,
	/**
	 * Não existe Fat_Proced_Hosp_Internos com este Código.
	 */
	FAT_00577,
	/**
	 * Não existe Fat_Espelhos_Aih com este Código.
	 */
	FAT_00578,
	/**
	 * Não existe procedimento ambulatorial para este codigo.
	 */
	FAT_00579,
	/**
	 * Não existe Tipo Aih com este Tah_Seq.
	 */
	FAT_00580,
	/**
	 * Não existe Procedimento Hospitalar com este Iph_Pho_Seq.
	 */
	FAT_00581,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00582,
	/**
	 * Não existe Proced Hosp Interno com este .
	 */
	FAT_00583,
	/**
	 * Não existe Proced Amb Realizado com este Seq.
	 */
	FAT_00584,
	/**
	 * Data de Realização do Item não Pode ser Maior que a Data de Alta.
	 */
	FAT_00585,
	/**
	 * Data de Realização do Item não Pode ser Menor que a Data de Internacao.
	 */
	FAT_00586,
	/**
	 * Número de AIH Inválido.
	 */
	FAT_00587,
	/**
	 * Item correspondente ao Procedimento Realizado da Conta nao pode ser Cancelado.
	 */
	FAT_00588,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00589,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00590,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00591,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00592,
	/**
	 * Não existe V_Fat_Contas_Hosp_Pacientes com este Cth_Csp_Cnv_Codigo, Cth_Csp_Seq, Cth_Phi_Seq.
	 */
	FAT_00593,
	/**
	 * Não existe V_Fat_Contas_Hosp_Pacientes com este Cth_Csp_Cnv_Codigo, Cth_Csp_Seq, Cth_Phi_Seq_Realizado.
	 */
	FAT_00594,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00595,
	/**
	 * Não existe V_Fat_Contas_Hosp_Pacientes com este Pac_Codigo.
	 */
	FAT_00596,
	/**
	 * Valor não existe no domínio Tipo Plano
	 */
	FAT_00597,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00598,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00599,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00600,
	/**
	 * Não existe Motivo Cobranca Apac com este Codigo.
	 */
	FAT_00601,
	/**
	 * Conta já encerrada, não pode receber novos itens.
	 */
	FAT_00602,
	/**
	 * Não foi possível Atualizar CID da Conta para o SSM Informado
	 */
	FAT_00603,
	/**
	 * Existe mais de 1 CID Associado ao Procedimento.
	 */
	FAT_00604,
	/**
	 * Não existe Fat_Itens_Conta_Hospitalar com este Conta.
	 */
	FAT_00605,
	/**
	 * Não existe Documento Cobranca Aih com este .
	 */
	FAT_00606,
	/**
	 * Mensagem Log com este Erro, Modulo, Situacao já cadastrado.
	 */
	FAT_00607,
	/**
	 * Não existe V_Fat_Contas_Hospitalar_Pac com este Cth_Csp_Cnv_Codigo, Cth_Csp_Seq, Cth_Phi_Seq.
	 */
	FAT_00608,
	/**
	 * Não existe V_Fat_Contas_Hospitalar_Pac com este Cth_Csp_Cnv_Codigo, Cth_Csp_Seq, Cth_Phi_Seq_Realizado.
	 */
	FAT_00609,
	/**
	 * Não existe Situacao Saida Paciente com este Cth_Sia_Msp_Seq, Cth_Sia_Seq.
	 */
	FAT_00610,
	/**
	 * Não existe Motivo Saida Paciente com este .
	 */
	FAT_00611,
	/**
	 * Valor Conta Hospitalar com este Código já cadastrado.
	 */
	FAT_00612,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00613,
	/**
	 * Não foi possivel inserir dados em ambulatorio.
	 */
	FAT_00614,
	/**
	 * Não existe Atendimento
	 */
	FAT_00615,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00616,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00617,
	/**
	 * Valor não existe no domínio Modo Lancamento Fat
	 */
	FAT_00618,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00619,
	/**
	 * Conta já Faturada/Encerrada.
	 */
	FAT_00620,
	/**
	 * Conta já possui motivo de saída.
	 */
	FAT_00621,
	/**
	 * Valor não existe no domínio Tipo Alta Uti
	 */
	FAT_00622,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00623,
	/**
	 * Conta não pode ter AIH e não ter Procedimento Solicitado.
	 */
	FAT_00624,
	/**
	 * Procedimento não está ativo.
	 */
	FAT_00625,
	/**
	 * Não existe Fat_Contas_Hospitalares com este Código, Seq.
	 */
	FAT_00626,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00627,
	/**
	 * Proced Amb Realizado com este Seq já cadastrado.
	 */
	FAT_00628,
	/**
	 * Item Centro Custo com este Tipo tabela, Procedimento, Código já cadastrado.
	 */
	FAT_00629,
	/**
	 * Item Centro Custo com este Tipo tabela, Procedimento já cadastrado.
	 */
	FAT_00630,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00631,
	/**
	 * Não existe Centro Custo com este Código.
	 */
	FAT_00632,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00633,
	/**
	 * Não existe Paciente com este Codigo.
	 */
	FAT_00634,
	/**
	 * Não existe Competencia com este Cpe_Modulo, Cpe_Dt_Hr_Inicio, Cpe_Ano, Cpe_Mes.
	 */
	FAT_00635,
	/**
	 * Caract_Item_Proc_Hosp com este Tipo tabela, Procedimento, Caracteristica já cadastrado.
	 */
	FAT_00636,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00637,
	/**
	 * Exc Cnv Grp Item Proc
	 */
	FAT_00638,
	/**
	 * Não existe Proced Hosp Interno com este .
	 */
	FAT_00639,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00640,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00641,
	/**
	 * Não existe Conv Proced Grupo
	 */
	FAT_00642,
	/**
	 * Não foi possível abrir Conta Hospitalar.
	 */
	FAT_00643,
	/**
	 * Paciente não possui Transplante.
	 */
	FAT_00644,
	/**
	 * Exc Cnv Grp Item Proc
	 */
	FAT_00645,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00646,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00647,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00648,
	/**
	 * Não existe Conv Proced Grupo
	 */
	FAT_00649,
	/**
	 * Procedimento necessita ser de Acompanhamento Pós-Transplante.
	 */
	FAT_00650,
	/**
	 * Paciente já tem Conta Aberta.
	 */
	FAT_00651,
	/**
	 * Não fio ossível gerar nova sequência para a Conta!
	 */
	FAT_00652,
	/**
	 * Não existe Fat_Exc_Cnv_Grp_Itens_Proc com este Código, Seq, Código, Grupo.
	 */
	FAT_00653,
	/**
	 * Conta Pac Transplante com este Codigo, Codigo, Dt Inscricao Transpante, Código já cadastrado.
	 */
	FAT_00654,
	/**
	 * Não existe Paciente Transplante com este Codigo, Codigo, Dt Inscricao Transpante.
	 */
	FAT_00655,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00656,
	/**
	 * Perda Item Conta com este Seqp, Código já cadastrado.
	 */
	FAT_00657,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00658,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00659,
	/**
	 * Não foi possivel criar conta de acompanhamento
	 */
	FAT_00660,
	/**
	 * Compat Exclus Acomp com este Tipo tabela, Procedimento, Tipo tabela, Procedimento, Ind Comparacao, Ind Compat Exclus, Codigo já cadastrado.
	 */
	FAT_00661,
	/**
	 * Não existe Compat Exclus Item com este Tipo tabela, Procedimento, Tipo tabela, Procedimento, Ind Comparacao, Ind Compat Exclus.
	 */
	FAT_00662,
	/**
	 * Não existe Tipo Transplante com este Codigo.
	 */
	FAT_00663,
	/**
	 * Não existe Paciente Transplante com este Pac_Codigo, Ptr_Dt_Inscricao_Transplante, Ptr_Ttr_Codigo.
	 */
	FAT_00664,
	/**
	 * Agrup Item Conta com este Código, Código já cadastrado.
	 */
	FAT_00665,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00666,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00667,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00668,
	/**
	 * Não existe Proced Amb Realizado com este Seq.
	 */
	FAT_00669,
	/**
	 * Não existe Fat_Conta_Apacs com este APAC.
	 */
	FAT_00670,
	/**
	 * V_Fat_Atend_Apac_Pacientes com este Atm_Numero já cadastrado.
	 */
	FAT_00671,
	/**
	 * Não existe Paciente com este Pac_Codigo.
	 */
	FAT_00672,
	/**
	 * Não existe V_Fat_Atend_Apac_Pacientes com este Atm_Phi_Seq.
	 */
	FAT_00673,
	/**
	 * Valor não existe no domínio Tipo Idade Uti
	 */
	FAT_00674,
	/**
	 * Saldo Uti com este Mes, Ano, Tipo Uti já cadastrado.
	 */
	FAT_00675,
	/**
	 * Saldo Uti com este Mes, Ano, Tipo Uti já cadastrado.
	 */
	FAT_00676,
	/**
	 * Valor não existe no domínio Tipo Idade Uti
	 */
	FAT_00677,
	/**
	 * Mês deve estar entre 1 e 12
	 */
	FAT_00678,
	/**
	 * Caract Item Proc Hosp com este Tipo tabela, Procedimento, Seq já cadastrado.
	 */
	FAT_00679,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00680,
	/**
	 * Não existe Tipo Caract Item com este Seq.
	 */
	FAT_00681,
	/**
	 * Tipo Caract Item com este Seq já cadastrado.
	 */
	FAT_00682,
	/**
	 * Tratamento Apac Caract com este Tipo tabela, Procedimento, Tipo tabela, Procedimento, Seq já cadastrado.
	 */
	FAT_00683,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00684,
	/**
	 * Não existe Caract Item Proc Hosp com este Tipo tabela, Procedimento, Seq.
	 */
	FAT_00685,
	/**
	 * Capacidade deve ser maior que o Saldo.
	 */
	FAT_00686,
	/**
	 * Diaria Uti Digitada
	 */
	FAT_00687,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00688,
	/**
	 * Tipo Tratamento com este Seq já cadastrado.
	 */
	FAT_00689,
	/**
	 * Atedimento Apac Proc Hospitalar com este APAC, Código, Tipo tabela, Procedimento já cadastrado.
	 */
	FAT_00690,
	/**
	 * Não existe Atendimento Apac com este APAC.
	 */
	FAT_00691,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00692,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00693,
	/**
	 * Valor não existe no domínio Prioridade
	 */
	FAT_00694,
	/**
	 * Deve haver somente um procedimento principal para a APAC
	 */
	FAT_00695,
	/**
	 * Deve ser informado um procedimento como principal para a APAC
	 */
	FAT_00696,
	/**
	 * Deve ser informado pelo menos um procedimento por APAC
	 */
	FAT_00697,
	/**
	 * Finalidade Trat Proc com este Codigo, Tipo tabela, Procedimento já cadastrado.
	 */
	FAT_00698,
	/**
	 * Não existe Finalidade Trat com este Codigo.
	 */
	FAT_00699,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00700,
	/**
	 * Procedimento Principal não possui este CID associado.
	 */
	FAT_00701,
	/**
	 * Não é possível excluir esta APAC. Já possui contas apresentadas
	 */
	FAT_00703,
	/**
	 * Não existe Competencia complementar com este Modulo, Mes, Ano, Dt Hr Inicio.
	 */
	FAT_00704,
	/**
	 * Não existe Servidor cadastrado com matricula/vinculo
	 */
	FAT_00705,
	/**
	 * Data de óbito/alta deve estar dentro do período da APAC.
	 */
	FAT_00706,
	/**
	 * APAC não tem data de inicio na CPE anterior
	 */
	FAT_00707,
	/**
	 * Conta APAC não tem data de inicio na CPE anterior
	 */
	FAT_00708,
	/**
	 * Conta APAC não está na competencia ativa
	 */
	FAT_00709,
	/**
	 * O ciclo previsto não está informado corretamente
	 */
	FAT_00710,
	/**
	 * Valor não existe no domínio Tipo Idade Uti
	 */
	FAT_00711,
	/**
	 * O procedimento informado não está relacionado a especialidade de consultas
	 */
	FAT_00712,
	/**
	 * Possibilidade Realizado com este Tipo tabela, Procedimento, Tipo tabela, Procedimento, Possibilidade já cadastrado.
	 */
	FAT_00713,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00714,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00715,
	/**
	 * Não encontrado procedimento principal para a APAC
	 */
	FAT_00716,
	/**
	 * Não encontrado procedimento secundário informado
	 */
	FAT_00717,
	/**
	 * Procedimento secundário não é compatível com o principal.
	 */
	FAT_00718,
	/**
	 * Esta APAC já teve uma conta enviada com esta situação. Não podem ser alterados procedimentos
	 */
	FAT_00719,
	/**
	 * Motivo Rejeicao com este Seq já cadastrado.
	 */
	FAT_00720,
	/**
	 * Valor não existe no domínio Situacao_Registro
	 */
	FAT_00721,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00722,
	/**
	 * Erro ao obter parâmetro referente ao CID do exame
	 */
	FAT_00723,
	/**
	 * Data de início da APAC não deve ser alterada
	 */
	FAT_00724,
	/**
	 * Para incluir APAC informe CID
	 */
	FAT_00725,
	/**
	 * A prioridade do CID deve ser Principal
	 */
	FAT_00726,
	/**
	 * Valor não existe no domínio Situação Datasus
	 */
	FAT_00727,
	/**
	 * Já existe lançamento para datasus com este SEQ
	 */
	FAT_00728,
	/**
	 * Não existe exame/item com estes códigos
	 */
	FAT_00729,
	/**
	 * Não existe consulta com este número
	 */
	FAT_00730,
	/**
	 * Não existe paciente com este código
	 */
	FAT_00731,
	/**
	 * Não existe tipo transplante com este código
	 */
	FAT_00732,
	/**
	 * Não existe especialidade com esta sigla
	 */
	FAT_00733,
	/**
	 * Já foi incluído relacionamento deste transplante / especialidade
	 */
	FAT_00734,
	/**
	 * Valor não existe no domínio Situação Lançado
	 */
	FAT_00735,
	/**
	 * Acerto AIH com este NRO AIH, IPH_PHO_SEQ e IPH_SEQ já cadastrado
	 */
	FAT_00736,
	/**
	 * Não existe Uf com este Sigla.
	 */
	FAT_00737,
	/**
	 * Periodo Emissao com este Código, Seq, Seq já cadastrado.
	 */
	FAT_00738,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_00739,
	/**
	 * Acerto Ambulatório com esta DT Comp., IPH_PHO_SEQ e IPH_SEQ já cadastrado
	 */
	FAT_00740,
	/**
	 * Item Proced Hosp Transplante com este Tipo tabela, Procedimento, Codigo já cadastrado.
	 */
	FAT_00741,
	/**
	 * Não existe Tipo Transplante com este Codigo.
	 */
	FAT_00742,
	/**
	 * Não existe Item Proced Hospitalar com este código
	 */
	FAT_00743,
	/**
	 * Nro AIH Transplante só deve ser informado se data do transplante estiver informada
	 */
	FAT_00744,
	/**
	 * A data do transplante deve ser maior ou igual a data da inscrição
	 */
	FAT_00745,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00746,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00747,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00748,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00749,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00750,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00751,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00752,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00753,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00754,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00755,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00756,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00757,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00758,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00759,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00760,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00761,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00762,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00763,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00764,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00765,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00766,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00767,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00768,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00769,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00770,
	/**
	 * Não existe esta unidade funcional
	 */
	FAT_00771,
	/**
	 * Não existe Proced Amb Realizado com este Seq.
	 */
	FAT_00772,
	/**
	 * Faturamento Resposta Ael com este Tabela Fat, Atributo já cadastrado.
	 */
	FAT_00773,
	/**
	 * Não existe Questao Questionario com este Qao Seq, Qtn Seq.
	 */
	FAT_00774,
	/**
	 * O procedimento já é principal de APAC ou cobrado em BPI.
	 */
	FAT_00775,
	/**
	 * O procedimento já é principal de APAC.
	 */
	FAT_00776,
	/**
	 * O procedimento já é cobrado como siscolo ,BPA ou BPI.
	 */
	FAT_00777,
	/**
	 * Valor não existe no domínio Tipo_Nutr_Parenteral
	 */
	FAT_00778,
	/**
	 * Não existe Componente Movimentado com este Código, Componente, Bolsa, Banco, Data, Seqüência.
	 */
	FAT_00779,
	/**
	 * Não existe Prescricao Procedimento com este Seq, Seq.
	 */
	FAT_00780,
	/**
	 * Não existe Motivo Rejeicao com este Rjc_Seq.
	 */
	FAT_00781,
	/**
	 * Não existe Especialidade
	 */
	FAT_00782,
	/**
	 * Não permitida inclusão.Excedida quantidade de APACs por ano para este paciente.
	 */
	FAT_00783,
	/**
	 * Erro ao obter parâmetro procedimento principal para apac de córnea
	 */
	FAT_00784,
	/**
	 * Não é permitida modificação nestes dados.
	 */
	FAT_00785,
	/**
	 * A data de encerramento da estatística deve ser informada somente após encerramento do faturamento
	 */
	FAT_00786,
	/**
	 * Acesso não permitido para esta aplicação
	 */
	FAT_00787,
	/**
	 * Já existe espelho siscolo para este procedimento nesta competência
	 */
	FAT_00788,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00789,
	/**
	 * Valor não existe no domínio Periodo_Emissao
	 */
	FAT_00790,
	/**
	 * Resultado Exame Siscolo com este Codigo Siscolo já cadastrado.
	 */
	FAT_00791,
	/**
	 * Não existe Resultado Exame Siscolo com este Codigo Siscolo.
	 */
	FAT_00792,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00793,
	/**
	 * Situação do Plano deve estar Ativa
	 */
	FAT_00794,
	/**
	 * Paciente transplantado. Precisa Laudo para APAC de acompanhamento
	 */
	FAT_00796,
	/**
	 * Paciente possui APAC de acompanhamento em andamento
	 */
	FAT_00797,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00798,
	/**
	 * Valor não existe no domínio Modulo Competencia
	 */
	FAT_00799,
	/**
	 * Erro na obtenção do parâmetro P_CID_APAC_FOTO
	 */
	FAT_00800,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00801,
	/**
	 * Fonema Conv Saude com este já cadastrado.
	 */
	FAT_00802,
	/**
	 * Não existe Fonema com este Fonema.
	 */
	FAT_00803,
	/**
	 * Não existe Convenio Saude com este Código.
	 */
	FAT_00804,
	/**
	 * Estadio Tumor com este Estadio já cadastrado.
	 */
	FAT_00805,
	/**
	 * Não encontrada competência aberta para módulo AMB
	 */
	FAT_00806,
	/**
	 * Não encontrada última competência encerrada para módulo AMB
	 */
	FAT_00807,
	/**
	 * Não encontrada competência encerrada ok para algum dos módulos de APAC
	 */
	FAT_00808,
	/**
	 * Apenas um entre Dado Conta Sem Int, Internacao
	 */
	FAT_00809,
	/**
	 * Existe conta
	 */
	FAT_00810,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00811,
	/**
	 * Informe código do paciente ou prontuário para pesquisa
	 */
	FAT_00812,
	/**
	 * Campos planejados deve ser maior que Campos Sessão e os dois devem ser informados
	 */
	FAT_00813,
	/**
	 * Prontuário com este número não cadastrado
	 */
	FAT_00814,
	/**
	 * Os campos planejados da APAC devem ser menores do que a soma dos campos planejados dos cids secundários
	 */
	FAT_00815,
	/**
	 * Tipo Trat Finalidade
	 */
	FAT_00816,
	/**
	 * Não existe Tipo Tratamento com este Seq.
	 */
	FAT_00817,
	/**
	 * Não existe Finalidade Trat com este Codigo.
	 */
	FAT_00818,
	/**
	 * Não foi informado CID principal para esta APAC. Informe.
	 */
	FAT_00819,
	/**
	 * Cid informado exige Estádio. Informe.
	 */
	FAT_00820,
	/**
	 * Não é permitido incluir apac para paciente sem cpf
	 */
	FAT_00821,
	/**
	 * Informe Estadio para o tratamento
	 */
	FAT_00822,
	/**
	 * Informe Finalidade para o tratamento
	 */
	FAT_00823,
	/**
	 * O paciente já possui APAC aberta para este tratamento no período
	 */
	FAT_00824,
	/**
	 * Proced Tratamento
	 */
	FAT_00825,
	/**
	 * Não existe Tipo Tratamento com este Seq.
	 */
	FAT_00826,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00827,
	/**
	 * Não existe Servidor
	 */
	FAT_00828,
	/**
	 * Data do diagnóstico deve ser menor que o ínicio do tratamento
	 */
	FAT_00829,
	/**
	 * Data de tratamento anterior deve ser menor que o atual
	 */
	FAT_00830,
	/**
	 * Paciente sem apac não precisa datas
	 */
	FAT_00831,
	/**
	 * Apac Campo Sessao com este APAC, Dt Inicio já cadastrado.
	 */
	FAT_00832,
	/**
	 * Não existe Atendimento Apac com este APAC.
	 */
	FAT_00833,
	/**
	 * Não existe Servidor
	 */
	FAT_00834,
	/**
	 * Informe quantidade de campos por sessão
	 */
	FAT_00835,
	/**
	 * A data de início deve estar no período da apac
	 */
	FAT_00836,
	/**
	 * Existe informação de campos posterior a esta data de fim.
	 */
	FAT_00837,
	/**
	 * Informação de campos deve ter início na competência aberta
	 */
	FAT_00838,
	/**
	 * Não encontrada competência aberta para APAC de Radioterapia
	 */
	FAT_00839,
	/**
	 * Somente é permitida inclusão/exclusão para ocorrências mais atuais
	 */
	FAT_00840,
	/**
	 * Motivo Pendencia com este Seq já cadastrado.
	 */
	FAT_00841,
	/**
	 * Pendencia Conta Hospitalar com este já cadastrado.
	 */
	FAT_00842,
	/**
	 * Não existe Servidor
	 */
	FAT_00843,
	/**
	 * Não existe Servidor
	 */
	FAT_00844,
	/**
	 * Não existe Motivo Pendencia com este Seq.
	 */
	FAT_00845,
	/**
	 * Não existe Unidade Funcional
	 */
	FAT_00846,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_00847,
	/**
	 * Existe conta encerrada para esta APAC. Não permitida alteração
	 */
	FAT_00848,
	/**
	 * Não existe Fat_Pendencias_Conta_Hosp com este Conta.
	 */
	FAT_00849,
	/**
	 * Acerta Cid Apac com este Cid já cadastrado.
	 */
	FAT_00850,
	/**
	 * Cid não permite informação de estádio
	 */
	FAT_00851,
	/**
	 * Cid exige informação do estádio
	 */
	FAT_00852,
	/**
	 * Cid não permitido para sexo do paciente
	 */
	FAT_00853,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_00854,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00855,
	/**
	 * Paciente não possui APAC em andamento
	 */
	FAT_00856,
	/**
	 * Paciente não possui APAC autorizada para o período desejado
	 */
	FAT_00857,
	/**
	 * Paciente não possui APAC autorizada
	 */
	FAT_00858,
	/**
	 * Indicador de validade quando informado deve ser I para internação e A para ambulatório
	 */
	FAT_00859,
	/**
	 * Tipo Cobranca com este já cadastrado.
	 */
	FAT_00860,
	/**
	 * Não existe Tipo Aih com este Codigo.
	 */
	FAT_00861,
	/**
	 * Não existe Motivo Saida Paciente com este Seq.
	 */
	FAT_00862,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00863,
	/**
	 * Valor não existe no domínio Cobranca
	 */
	FAT_00864,
	/**
	 * Não existe Pagador com este Seq.
	 */
	FAT_00865,
	/**
	 * Motivo Desdobr Ssm com este Codigo, Procedimento, Tipo tabela já cadastrado.
	 */
	FAT_00866,
	/**
	 * Não existe Motivo Desdobramento com este Codigo.
	 */
	FAT_00867,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_00868,
	/**
	 * Não existe Convenio Saude com este Código.
	 */
	FAT_00869,
	/**
	 * Não existe Especialidade
	 */
	FAT_00870,
	/**
	 * Informe o Mes e Ano da Apresentação
	 */
	FAT_00873,
	/**
	 * Mes e Ano da Apresentação não devem ser modificados
	 */
	FAT_00874,
	/**
	 * Mes e Ano da Apresentação devem ser nulos
	 */
	FAT_00875,
	/**
	 * Não é permitida inclusão de apac com início após a data do óbito do paciente
	 */
	FAT_00876,
	/**
	 * Informar Fator de Conversão
	 */
	FAT_00878,
	/**
	 * Informar Operação de Conversão
	 */
	FAT_00879,
	/**
	 * Valor para a coluna TIPO_OPER_CONVERSAO não está no domínio OPERACAO_CONVERSAO- Alteração
	 */
	FAT_00882,
	/**
	 * Candidato Apac Otorrino com este Seq já cadastrado.
	 */
	FAT_00883,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00884,
	/**
	 * Não existe Proced Amb Realizado com este Seq.
	 */
	FAT_00885,
	/**
	 * Não existe Consulta com este número
	 */
	FAT_00886,
	/**
	 * Não existe Cirurgia
	 */
	FAT_00887,
	/**
	 * Não existe Servidor
	 */
	FAT_00888,
	/**
	 * Não existe Equipe com este Código.
	 */
	FAT_00889,
	/**
	 * Não existe Paciente com este Codigo.
	 */
	FAT_00890,
	/**
	 * Não existe Servidor
	 */
	FAT_00891,
	/**
	 * Não existe Servidor
	 */
	FAT_00892,
	/**
	 * Erro ao acessar parâmetro P_DT_OTORRINO
	 */
	FAT_00893,
	/**
	 * Erro ao inserir fat_candidatos_apac_otorrino - CONSULTAS Erro - {1}
	 */
	FAT_00894,
	/**
	 * Erro ao inserir fat_candidatos_apac_otorrino - PMR.Erro - {1}
	 */
	FAT_00895,
	/**
	 * Erro ao inserir fat_candidatos_apac_otorrino - CIRURGIAS.Erro - {1}
	 */
	FAT_00896,
	/**
	 * Erro ao atualizar parâmetro P_DT_OTORRINO
	 */
	FAT_00897,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00898,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00899,
	/**
	 * Resumo Apac com este Codigo, Atm Numero já cadastrado.
	 */
	FAT_00900,
	/**
	 * Não existe Paciente com este Codigo.
	 */
	FAT_00901,
	/**
	 * Erro ao acessar parâmetro com PHI de DIAGNOSTICO. Procedure fatp_candidato_apac
	 */
	FAT_00902,
	/**
	 * Não existe este Procedimento no cadastro
	 */
	FAT_00903,
	/**
	 * Erro ao acessar parâmetro P_PHI_SELECAO. Procedure fatp_candidato_apac
	 */
	FAT_00904,
	/**
	 * Não existe cid com este código
	 */
	FAT_00905,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00906,
	/**
	 * Erro ao acessar parâmetro P_PHI_IMPLANTE_COCLEAR. Procedure fatp_candidato_apac
	 */
	FAT_00907,
	/**
	 * Erro ao acessar parâmetro PHI de ACOMP ADAPTADO. Procedure fatp_candidato_apac
	 */
	FAT_00908,
	/**
	 * Erro ao acessar parâmetro P_PHI_NADAPTADO. Procedure fatp_candidato_apac
	 */
	FAT_00909,
	/**
	 * Erro ao acessar parâmetro PHI de ACOMP IMPLANTE. Procedure fatp_candidato_apac
	 */
	FAT_00910,
	/**
	 * Deve ser informado pelo menos um cid por APAC
	 */
	FAT_00911,
	/**
	 * Informe o profissional para a APAC
	 */
	FAT_00912,
	/**
	 * Informe o prontuário do paciente ou a unidade.
	 */
	FAT_00913,
	/**
	 * Não é possível execução para uma competência já encerrada
	 */
	FAT_00914,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00915,
	/**
	 * Procedimento incompatível com idade do paciente
	 */
	FAT_00916,
	/**
	 * Erro ao acessar parâmetro P_ESP_GENER_OTO
	 */
	FAT_00917,
	/**
	 * Número existe no banco de dados histórico. Já foi utilizado
	 */
	FAT_00918,
	/**
	 * Este procedimento não pode ser Principal, somente Secundário
	 */
	FAT_00919,
	/**
	 * Laudo já foi impresso e não pode ser desconsiderado
	 */
	FAT_00920,
	/**
	 * Erro ao excluir o laudo na opção de laudo desconsiderado
	 */
	FAT_00921,
	/**
	 * Não existe Servidor
	 */
	FAT_00922,
	/**
	 * Valor não existe no domínio Situacao_Nro_Apac
	 */
	FAT_00923,
	/**
	 * O paciente já possui APAC de aparelho com este procedimento no período
	 */
	FAT_00924,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00925,
	/**
	 * Esta conta já possui AIH informada. O procedimento solicitado não pode ser alterado.
	 */
	FAT_00926,
	/**
	 * Paciente já possui APAC no período.
	 */
	FAT_00927,
	/**
	 * O paciente já possui APAC aberta para este tratamento no período
	 */
	FAT_00928,
	/**
	 * Parâmetro com data final da competência não encontrado.
	 */
	FAT_00929,
	/**
	 * Data de início da APAC/Conta não pode ser maior que o final da competência
	 */
	FAT_00930,
	/**
	 * Não informar alta para APACs de acompanhemento.Alta informada somente pelo sistema em caso de óbito.
	 */
	FAT_00931,
	/**
	 * Data alta não pode ser maior que o final previsto da competência
	 */
	FAT_00932,
	/**
	 * Erro ao acessar parâmetro com PHI de REAVALIAÇÃO. Procedimento fatp_candidato_apac
	 */
	FAT_00933,
	/**
	 * Erro ao acessar parâmetro com PHI de TERAPIA. Procedimento fatp_candidato_apac
	 */
	FAT_00934,
	/**
	 * Erro ao acessar parâmetro P_DT_FIM_COMP_APT
	 */
	FAT_00935,
	/**
	 * Não existe Resumo Apac com este .
	 */
	FAT_00936,
	/**
	 * Não existe Proced Hosp Interno com este .
	 */
	FAT_00937,
	/**
	 * Fat_Sub_Grupo_Item_Conta com este Codigo já cadastrado.
	 */
	FAT_00938,
	/**
	 * Valor não existe no domínio Tipo Laudo Sms
	 */
	FAT_00939,
	/**
	 * Fat_Autorizados_Cma com este Cth Seq, Cod Sus Cma já cadastrado.
	 */
	FAT_00940,
	/**
	 * Não é permitido incluir esta apac para paciente sem cartão nacional de saúde
	 */
	FAT_00941,
	/**
	 * Este procedimento está validando uma APAC de otorrino. Exclua-a primeiro.
	 */
	FAT_00942,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_00943,
	/**
	 * Porte Cirurgico com este Seq já cadastrado.
	 */
	FAT_00944,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_00945,
	/**
	 * Valor Porte Cirurgico com este Seq, Inicio Validade já cadastrado.
	 */
	FAT_00946,
	/**
	 * Não existe Porte Cirurgico com este Seq.
	 */
	FAT_00947,
	/**
	 * Coeficiente Hospitalar com este Seq já cadastrado.
	 */
	FAT_00948,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_00949,
	/**
	 * Agrupamento com este Seq já cadastrado.
	 */
	FAT_00950,
	/**
	 * Tabela Pagamento com este Seq já cadastrado.
	 */
	FAT_00951,
	/**
	 * Laboratorio Brasindice com este Seq já cadastrado.
	 */
	FAT_00952,
	/**
	 * Lab Brasind X Lab Mat com este Seq, Código já cadastrado.
	 */
	FAT_00953,
	/**
	 * Não existe Laboratorio Brasindice com este Seq.
	 */
	FAT_00954,
	/**
	 * Não existe Marca Comercial com este Código.
	 */
	FAT_00955,
	/**
	 * Proced Tab Pagto com este Seq já cadastrado.
	 */
	FAT_00956,
	/**
	 * Não existe Laboratorio Brasindice com este Seq.
	 */
	FAT_00957,
	/**
	 * Não existe Tabela Pagamento com este Seq.
	 */
	FAT_00958,
	/**
	 * Ch Proced Pagto com este Seq, Inicio Validade já cadastrado.
	 */
	FAT_00959,
	/**
	 * Não existe Proced Tab Pagto com este Seq.
	 */
	FAT_00960,
	/**
	 * Phi X Proced Tab Pagto com este Código, Seq já cadastrado.
	 */
	FAT_00961,
	/**
	 * Não existe Proced Tab Pagto com este Seq.
	 */
	FAT_00962,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00963,
	/**
	 * Agrupamento Proced Conv com este Seq já cadastrado.
	 */
	FAT_00964,
	/**
	 * Agrupamento Proced Conv com este Seq, Seq, Seq já cadastrado.
	 */
	FAT_00965,
	/**
	 * Não existe Proced Tab Pagto com este Seq.
	 */
	FAT_00966,
	/**
	 * Não existe Convenio Saude com este .
	 */
	FAT_00967,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_00968,
	/**
	 * Não existe Agrupamento com este Seq.
	 */
	FAT_00969,
	/**
	 * Não existe Agrupamento com este Seq.
	 */
	FAT_00970,
	/**
	 * Pacote com este Seq já cadastrado.
	 */
	FAT_00971,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00972,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_00973,
	/**
	 * Parametro Pacote com este Seq já cadastrado.
	 */
	FAT_00974,
	/**
	 * Não existe Pacote com este Seq.
	 */
	FAT_00975,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_00976,
	/**
	 * Parametros Pacotes com este Seq já cadastrado.
	 */
	FAT_00977,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_00978,
	/**
	 * Não existe Pacote com este Seq.
	 */
	FAT_00979,
	/**
	 * Agrupamento Interno Proced com este Seq já cadastrado.
	 */
	FAT_00980,
	/**
	 * Não existe Agrupamento com este Seq.
	 */
	FAT_00981,
	/**
	 * Não existe Agrupamento com este Seq.
	 */
	FAT_00982,
	/**
	 * Não existe Porte Cirurgico com este Seq.
	 */
	FAT_00983,
	/**
	 * Não existe Coeficiente Hospitalar com este Seq.
	 */
	FAT_00984,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_00985,
	/**
	 * Não existe Tabela Pagamento com este Seq.
	 */
	FAT_00986,
	/**
	 * Não existe Proced Tab Pagto com este Seq.
	 */
	FAT_00987,
	/**
	 * Caracteristica Complexidade com este Seq já cadastrado.
	 */
	FAT_00988,
	/**
	 * Não existe Servidor
	 */
	FAT_00989,
	/**
	 * Não existe Servidor
	 */
	FAT_00990,
	/**
	 * Caract Financiamento com este Seq já cadastrado.
	 */
	FAT_00991,
	/**
	 * Não existe Servidor
	 */
	FAT_00992,
	/**
	 * Não existe Servidor
	 */
	FAT_00993,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00994,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_00995,
	/**
	 * Não existe Convenio Saude com este Cnv Codigo.
	 */
	FAT_00996,
	/**
	 * Valor não existe no domínio Tipo_Uso_Tratamento
	 */
	FAT_00997,
	/**
	 * Tipo Pendencia com este Seq já cadastrado.
	 */
	FAT_00998,
	/**
	 * Nota Fiscal com este Seq já cadastrado.
	 */
	FAT_00999,
	/**
	 * Nota Fiscal com este Numero já cadastrado.
	 */
	FAT_01000,
	/**
	 * Não existe Conta Hospitalar
	 */
	FAT_01001,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_01002,
	/**
	 * Motivo Glosa com este Seq já cadastrado.
	 */
	FAT_01003,
	/**
	 * Tipo Ocorrencia com este Seq já cadastrado.
	 */
	FAT_01004,
	/**
	 * Não existe Servidor
	 */
	FAT_01005,
	/**
	 * Item Nota Fiscal com este Seq já cadastrado.
	 */
	FAT_01006,
	/**
	 * Não existe Nota Fiscal com este Seq.
	 */
	FAT_01007,
	/**
	 * Não existe Fat_Itens_Nota_Fiscal com este Conta, Código.
	 */
	FAT_01008,
	/**
	 * Não existe Item Nota Fiscal com este Seq.
	 */
	FAT_01009,
	/**
	 * Não existe Item Nota Fiscal com este Seq.
	 */
	FAT_01010,
	/**
	 * Não existe Agrupamento Proced Conv com este Seq.
	 */
	FAT_01011,
	/**
	 * Não existe Proced Tab Pagto com este Seq.
	 */
	FAT_01012,
	/**
	 * Não existe Servidor
	 */
	FAT_01013,
	/**
	 * Pendencia Nota com este Seq, Seq, Seq já cadastrado.
	 */
	FAT_01014,
	/**
	 * Não existe Tipo Pendencia com este Seq.
	 */
	FAT_01015,
	/**
	 * Não existe Nota Fiscal com este Seq.
	 */
	FAT_01016,
	/**
	 * Não existe Item Nota Fiscal com este Seq.
	 */
	FAT_01017,
	/**
	 * Responsavel Nota com este Seq, Seq já cadastrado.
	 */
	FAT_01018,
	/**
	 * Não existe Nota Fiscal com este Seq.
	 */
	FAT_01019,
	/**
	 * Ocorrencia Nota com este Ocr Id já cadastrado.
	 */
	FAT_01020,
	/**
	 * Não existe Tipo Ocorrencia com este Seq.
	 */
	FAT_01021,
	/**
	 * Não existe Nota Fiscal com este Seq.
	 */
	FAT_01022,
	/**
	 * Não existe Item Nota Fiscal com este Seq.
	 */
	FAT_01023,
	/**
	 * Não existe Motivo Glosa com este Seq.
	 */
	FAT_01024,
	/**
	 * Fatura com este Seq já cadastrado.
	 */
	FAT_01025,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_01026,
	/**
	 * Nota Fatura com este Seq, Seq já cadastrado.
	 */
	FAT_01027,
	/**
	 * Não existe Fatura com este Seq.
	 */
	FAT_01028,
	/**
	 * Não existe Nota Fiscal com este Seq.
	 */
	FAT_01029,
	/**
	 * Item Pacote com este Seq, Código já cadastrado.
	 */
	FAT_01030,
	/**
	 * Não existe Pacote com este Seq.
	 */
	FAT_01031,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_01032,
	/**
	 * Pacote com este Seq já cadastrado.
	 */
	FAT_01033,
	/**
	 * Pacote com este Código já cadastrado.
	 */
	FAT_01034,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_01035,
	/**
	 * Este procedimentos não aceita o CID informado. Confira.
	 */
	FAT_01036,
	/**
	 * Não existe Tipo Alta Medica com este Código.
	 */
	FAT_01037,
	/**
	 * Não existe Situacao Saida Paciente com este Seq, Código.
	 */
	FAT_01038,
	/**
	 * Rel Mot Alta com este Seq já cadastrado.
	 */
	FAT_01039,
	/**
	 * Parametros Tabela com este Seq já cadastrado.
	 */
	FAT_01040,
	/**
	 * Não existe Coeficiente Hospitalar com este Seq.
	 */
	FAT_01041,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_01042,
	/**
	 * Não existe Convenio Saude com este Código.
	 */
	FAT_01043,
	/**
	 * Não existe Tabela Pagamento com este Seq.
	 */
	FAT_01044,
	/**
	 * Não existe Proced Tab Pagto com este Seq.
	 */
	FAT_01045,
	/**
	 * Não existe Coeficiente Hospitalar com este Seq.
	 */
	FAT_01046,
	/**
	 * Não existe Projeto Pesquisa
	 */
	FAT_01047,
	/**
	 * Parametros Procedimento com este Seq já cadastrado.
	 */
	FAT_01048,
	/**
	 * Não existe Porte Cirurgico com este Seq.
	 */
	FAT_01049,
	/**
	 * Não existe Proced Tab Pagto com este Seq.
	 */
	FAT_01050,
	/**
	 * Não existe Coeficiente Hospitalar com este Seq.
	 */
	FAT_01051,
	/**
	 * Não existe Convenio Saude com este Código.
	 */
	FAT_01052,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_01053,
	/**
	 * Valor Porte Cirurgico com este Seq, Inicio Validade já cadastrado.
	 */
	FAT_01054,
	/**
	 * Não existe Porte Cirurgico com este Seq.
	 */
	FAT_01055,
	/**
	 * Não existe Convenio Saude com este Código.
	 */
	FAT_01056,
	/**
	 * Não existe Conv Saude Plano com este Seq, Código.
	 */
	FAT_01057,
	/**
	 * Não existe Porte Cirurgico com este Pcr Seq.
	 */
	FAT_01058,
	/**
	 * Não existe Procedimento Hospitalar com este Pho Seq.
	 */
	FAT_01059,
	/**
	 * Excecao Parametros Tab com este Seq já cadastrado.
	 */
	FAT_01060,
	/**
	 * Não existe Convenio Saude com este Código.
	 */
	FAT_01061,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_01062,
	/**
	 * Não existe Projeto Pesquisa
	 */
	FAT_01063,
	/**
	 * Não existe Tabela Pagamento com este Seq.
	 */
	FAT_01064,
	/**
	 * Não existe Proced Tab Pagto com este Seq.
	 */
	FAT_01065,
	/**
	 * Valor Porte Cirurgico com este Seq já cadastrado.
	 */
	FAT_01066,
	/**
	 * Valor Porte Cirurgico com este Seq, Código, Seq, Inicio Validade já cadastrado.
	 */
	FAT_01067,
	/**
	 * Não existe Porte Cirurgico com este Seq.
	 */
	FAT_01068,
	/**
	 * Não existe Convenio Saude com este Código.
	 */
	FAT_01069,
	/**
	 * Não existe Conv Saude Plano com este Código, Seq.
	 */
	FAT_01070,
	/**
	 * Não existe Agrupamento com este Agr Seq.
	 */
	FAT_01071,
	/**
	 * Não existe Agrupamento com este Agr Seq.
	 */
	FAT_01072,
	/**
	 * Não existe Coeficiente Hospitalar com este Coh Seq Filme.
	 */
	FAT_01073,
	/**
	 * Paciente não encontrado no cadastro de pacientes
	 */
	FAT_01074,
	/**
	 * Data de diagnóstico informada não pode ser menor que data de nascimento do paciente.Confira
	 */
	FAT_01075,
	/**
	 * O procedimento já é cobrado como Principal de APAC ou BPA
	 */
	FAT_01076,
	/**
	 * Não existe Cuidado Usual com este Cdu Seq.
	 */
	FAT_01077,
	/**
	 * Não existe Cuidado com este Cui Seq.
	 */
	FAT_01078,
	/**
	 * Não existe item de exame proveniente de triagem com este código
	 */
	FAT_01079,
	/**
	 * Não existe item de medicamento proveniente de triagem com este código
	 */
	FAT_01080,
	/**
	 * Não existe Item Medicacao
	 */
	FAT_01081,
	/**
	 * Não existe Item Exame
	 */
	FAT_01082,
	/**
	 * Não existe Tipo Item Dieta com este Tid Seq.
	 */
	FAT_01083,
	/**
	 * Column IND_PRIORIDADE not in domain TIPO_CID
	 */
	FAT_01084,
	/**
	 * Column IND_PRIORIDADE not in domain TIPO_CID
	 */
	FAT_01085,
	/**
	 * Modalidade Atendimento com este Codigo já cadastrado.
	 */
	FAT_01086,
	/**
	 * Procedimento Modalidade com este Codigo, Procedimento, Tipo tabela já cadastrado.
	 */
	FAT_01087,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_01088,
	/**
	 * Não existe Modalidade Atendimento com este Codigo.
	 */
	FAT_01089,
	/**
	 * Não existe Unidade Funcional
	 */
	FAT_01090,
	/**
	 * Não existe Cid com este Código.
	 */
	FAT_01091,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_01092,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_01093,
	/**
	 * FAT_ACS_CK1
	 */
	FAT_01094,
	/**
	 * Não existe Cid com este Código.
	 */
	FAT_01095,
	/**
	 * Não informe um Cid para este procedimento.
	 */
	FAT_01096,
	/**
	 * Informe um Cid para este procedimento.
	 */
	FAT_01097,
	/**
	 * Não informe um Cid para este procedimento.
	 */
	FAT_01098,
	/**
	 * Informe um Cid para este procedimento.
	 */
	FAT_01099,
	/**
	 * Informe um Cid compatível com o procedimento. Verifique.
	 */
	FAT_01100,
	/**
	 * Informe um Cid compatível com o procedimento. Verifique.
	 */
	FAT_01101,
	/**
	 * Procedimento não existe com este código
	 */
	FAT_01102,
	/**
	 * Autoriza Apac com este Cpf já cadastrado.
	 */
	FAT_01103,
	/**
	 * Informe o indicador para ambulatório ou internação.
	 */
	FAT_01104,
	/**
	 * PHI de Nutrição Enteral deve ser compatível com a idade.
	 */
	FAT_01105,
	/**
	 * Não existe Prescricao Paciente
	 */
	FAT_01106,
	/**
	 * Não existe Prescricao Procedimento com este Pao Seq.
	 */
	FAT_01107,
	/**
	 * Não existe Servidor
	 */
	FAT_01108,
	/**
	 * Não existe Servidor
	 */
	FAT_01109,
	/**
	 * Não existe Servidor
	 */
	FAT_01110,
	/**
	 * Não existe Servidor
	 */
	FAT_01111,
	/**
	 * Não existe servidor cadastrado com este vinculo e matricula
	 */
	FAT_01112,
	/**
	 * Este anestesista não possui CBO cadastrado!
	 */
	FAT_01113,
	/**
	 * Erro ao recuperar parâmetro de sistema na procedure FATP_VER_CARGO_OCUP. Contate GSIS.{1}.
	 */
	FAT_01114,
	/**
	 * Erro ao recuperar parâmetro de sistema na procedure FATP_VER_CARGO_OCUP. Contate GSIS.{1}.
	 */
	FAT_01115,
	/**
	 * Não existe Tipo Item Dieta com este Tid Seq.
	 */
	FAT_01116,
	/**
	 * Proced Hosp Interno com este Tipo Nutricao Enteral já cadastrado.
	 */
	FAT_01117,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_01118,
	/**
	 * Valor não existe no domínio Tipo_Nutricao_Enteral
	 */
	FAT_01119,
	/**
	 * Proced Hosp Interno com este Tipo Nutrição Enteral já cadastrado.
	 */
	FAT_01120,
	/**
	 * Cad Cid Nascimento com este Seq já cadastrado.
	 */
	FAT_01121,
	/**
	 * Não existe Servidor
	 */
	FAT_01122,
	/**
	 * Não existe Servidor
	 */
	FAT_01123,
	/**
	 * Valor não existe no domínio Vivo_Morto
	 */
	FAT_01124,
	/**
	 * Valor não existe no domínio Vivo_Morto
	 */
	FAT_01125,
	/**
	 * Valor não existe no domínio Vivo_Morto
	 */
	FAT_01126,
	/**
	 * Valor não existe no domínio Vivo_Morto
	 */
	FAT_01127,
	/**
	 * Valor não existe no domínio Vivo_Morto
	 */
	FAT_01128,
	/**
	 * Valor não existe no domínio Vivo_Morto
	 */
	FAT_01129,
	/**
	 * Valor não existe no domínio Vivo_Morto
	 */
	FAT_01130,
	/**
	 * Valor não existe no domínio Vivo_Morto
	 */
	FAT_01131,
	/**
	 * Informe Estadio para o tratamento!
	 */
	FAT_01132,
	/**
	 * Informe Finalidade para o tratamento!
	 */
	FAT_01133,
	/**
	 * Banco Capacidade com este Ano, Mes, Clinica já cadastrado.
	 */
	FAT_01134,
	/**
	 * Não existe Servidor
	 */
	FAT_01135,
	/**
	 * Não existe Servidor
	 */
	FAT_01136,
	/**
	 * Erro ao acessar parâmetro com P_OTO_QTDE_REP. Procedimento fatp_candidato_apac
	 */
	FAT_01137,
	/**
	 * Phi_X_Phi com este Código, Código já cadastrado.
	 */
	FAT_01138,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_01139,
	/**
	 * Não existe Proced Hosp Interno com este Código.
	 */
	FAT_01140,
	/**
	 * Paciente_Processo_Transex com este Seq já cadastrado.
	 */
	FAT_01141,
	/**
	 * Paciente_Processo_Transex com este já cadastrado.
	 */
	FAT_01142,
	/**
	 * Não existe Paciente com este Pac Codigo.
	 */
	FAT_01143,
	/**
	 * Não existe Servidor
	 */
	FAT_01144,
	/**
	 * Espelho Sismama com este Esm Id já cadastrado.
	 */
	FAT_01145,
	/**
	 * Não existe Proced Amb Realizado com este Seq.
	 */
	FAT_01146,
	/**
	 * Não existe Unidade Funcional
	 */
	FAT_01147,
	/**
	 * Não existe Caracteristica Complexidade com este Seq.
	 */
	FAT_01148,
	/**
	 * Não existe Caract Financiamento com este Seq.
	 */
	FAT_01149,
	/**
	 * Não existe Competencia com este Modulo, Mes, Ano, Dt Hr Inicio.
	 */
	FAT_01150,
	/**
	 * Não existe Servidor
	 */
	FAT_01151,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_01152,
	/**
	 * Item Espelho Sismama com este Iem Id já cadastrado.
	 */
	FAT_01153,
	/**
	 * Não existe Espelho Sismama com este Esm Id.
	 */
	FAT_01154,
	/**
	 * Valor não existe no domínio Sim_Nao
	 */
	FAT_01155,
	/**
	 * Column CPE_MODULO not in domain MODULO COMPETENCIA
	 */
	FAT_01156,
	/**
	 * Column CPE_MODULO not in domain MODULO COMPETENCIA
	 */
	FAT_01157,
	/**
	 * Espelho Sismama com este Seq já cadastrado.
	 */
	FAT_01158,
	/**
	 * Não existe Proced Amb Realizado com este Seq.
	 */
	FAT_01159,
	/**
	 * Não existe Unidade Funcional
	 */
	FAT_01160,
	/**
	 * Não existe Caracteristica Complexidade com este Seq.
	 */
	FAT_01161,
	/**
	 * Não existe Caract Financiamento com este Seq.
	 */
	FAT_01162,
	/**
	 * Não existe Competencia com este Modulo, Mes, Ano, Dt Hr Inicio.
	 */
	FAT_01163,
	/**
	 * Não existe Servidor
	 */
	FAT_01164,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_01165,
	/**
	 * Apenas um entre Sismama Histo Res, Sismama Mamo Res, Sismama Cito Res pode ser especificado
	 */
	FAT_01166,
	/**
	 * Erro na FATK_AAP_RN.RN_AAPP_VER_APAC_RAD chamada da FATT_AAP_BRI. Contate a CGTI.
	 */
	FAT_01167,
	/**
	 * Exclusao Critica com este Seq já cadastrado.
	 */
	FAT_01169,
	/**
	 * Exclusao Critica com este Seq já cadastrado.
	 */
	FAT_01170,
	/**
	 * Exclusao Critica com este Codigo já cadastrado.
	 */
	FAT_01171,
	/**
	 * Exclusão de Crítica não cadastrada
	 */
	FAT_01172,
	/**
	 * Lista Pac Apac com este Seq já cadastrado.
	 */
	FAT_01173,
	/**
	 * Não existe Tipo Tratamento com este Seq.
	 */
	FAT_01174,
	/**
	 * Não existe Paciente com este Codigo.
	 */
	FAT_01175,
	/**
	 * Não existe Servidor
	 */
	FAT_01176,
	/**
	 * Não existe Servidor
	 */
	FAT_01177,
	/**
	 * Laudo Pac Apac com este Seq já cadastrado.
	 */
	FAT_01178,
	/**
	 * Não existe Servidor
	 */
	FAT_01179,
	/**
	 * Não existe Servidor
	 */
	FAT_01180,
	/**
	 * Não existe Servidor
	 */
	FAT_01181,
	/**
	 * Não existe Lista Pac Apac com este Seq.
	 */
	FAT_01182,
	/**
	 * Lista Pac Apac com este Seq já cadastrado.
	 */
	FAT_01183,
	/**
	 * Não existe Tipo Tratamento com este Seq.
	 */
	FAT_01184,
	/**
	 * Não existe Paciente com este Codigo.
	 */
	FAT_01185,
	/**
	 * Não existe Servidor
	 */
	FAT_01186,
	/**
	 * Não existe Servidor
	 */
	FAT_01187,
	/**
	 * Não existe Consulta com este Consulta.
	 */
	FAT_01188,
	/**
	 * Protocolo Laudo Apac com este Seq já cadastrado.
	 */
	FAT_01189,
	/**
	 * Não existe Servidor
	 */
	FAT_01190,
	/**
	 * Não existe Servidor
	 */
	FAT_01191,
	/**
	 * Laudo Protocolo Apac com este Seq já cadastrado.
	 */
	FAT_01192,
	/**
	 * Não existe Protocolo Laudo Apac com este Seq.
	 */
	FAT_01193,
	/**
	 * Não existe Laudo Pac Apac com este Seq.
	 */
	FAT_01194,
	/**
	 * Tipo Tratamento com este Seq já cadastrado.
	 */
	FAT_01195,
	/**
	 * Tipo Tratamento com este Cod Tabela já cadastrado.
	 */
	FAT_01196,
	/**
	 * Não existe Item Proced Hospitalar
	 */
	FAT_01197,
	/**
	 * Valor não existe no domínio Ati_Inat
	 */
	FAT_01198,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_01199,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_01200,
	/**
	 * Valor não existe no domínio Nao_Sim
	 */
	FAT_01201,
	/**
	 * Não existe Lista Pac Apac com este Seq.
	 */
	FAT_01202,
	/**
	 * Não existe Cid com este Cid.
	 */
	FAT_01203,
	/**
	 * Não existe Consulta com este Con Numero.
	 */
	FAT_01204,
	/**
	 * Existe laudo pendente de recebimento.
	 */
	FAT_01205,
	/**
	 * Existe laudo com pendência de APAC ou assinaturas.
	 */
	FAT_01206,
	/**
	 * Erro insert em FAT_PROTOCOLO_LAUDO_APACS em FATF_APAC_TP_TRAT. Contate CGTI. {1}.
	 */
	FAT_01207,
	/**
	 * Erro insert em FAT_LAUDOS_PROTOCOLO_APAC em FATF_APAC_TP_TRAT. Contate CGTI. {1}.
	 */
	FAT_01208,
	/**
	 * Protocolo gerado com sucesso: {1}.
	 */
	FAT_01209,
	/**
	 * Erro update em FAT_APACS em FATF_APAC_TP_TRAT. Contate CGTI. {1}.
	 */
	FAT_01210,
	/**
	 * Erro update em FAT_LAUDOS_PAC_APACS em FATF_APAC_TP_TRAT. Contate CGTI. {1}.
	 */
	FAT_01211,
	/**
	 * Erro delete em AAC_ATENDIMENTO_APACS em FATF_APAC_TP_TRAT. Contate CGTI. {1}.
	 */
	FAT_01212,
	/**
	 * Quando protocolo informado não informar outros critérios de restrição.
	 */
	FAT_01213,
	/**
	 * Os campos Tratamento ou Protocolo devem ser obrigatoriamente informados.
	 */
	FAT_01214,
	/**
	 * Procedimento não está ativo.
	 */
	FAT_0625,
	
	/*ABAIXO FORAM INSERIDAS CHAVES PARA MENSAGENS DE ERRO QUE NAO POSSUIAM CODIGO*/
	/**
	 * Conta Rejeitada deve possuir Motivo de Rejeição.
	 */
	CTA_REJEITADA,
	/**
	 * Somente Conta Rejeitada possui Motivo de Rejeição.
	 */
	CTA_REJEITADA_MOTIVO,
	/**
	 * Item de Conta Hospitalar já está faturado.
	 */
	FATT_ICH_BRD,
	/**
	 * ATENÇÃO! A pessoa {1} não possui CBO cadastrado
	 */
	SEM_CBO,
	/**
	 * Usuário não cadastrado como servidor.
	 */
	USUARIO_NAO_SERVIDOR,

	/**
	 * Convênio Plano Saúde inconsistente para módulo {0} e procedimento HCPA {1} 
	 */
	CONVENIO_PLANO_SAUDE_INCONSISTENTE_MODULO,
	
	/**
	 * Não foi possível localizar o parâmetro {0}
	 */
	PARAMETRO_INVALIDO,
	/**
	 * Uma conta pode ter somente um cid principal
	 */
	CONTA_MAIS_UM_CID_PRINCIPAL,
	/**
	 * Esse CID já foi cadastrado
	 */
	CID_JA_CADASTRADO,
	
	/**
	 * Conta Hospitalar não encontrada
	 */
	CONTA_HOSPITALAR_NAO_ENCONTRADA,
	
	/**
	 * Conta não reapresentada
	 */
	CONTA_NAO_REAPRESENTADA,
	
	/**
	 * Conta Internação não encontrada
	 */
	CONTA_INTERNACAO_NAO_ENCONTRADA,
	
	/**
	 * Erro ao desfazer internação da conta reapresentada
	 */
	ERRO_DESFAZER_INTERNACAO_CONTA_REAPRESENTADA,
		
	/**
	 * CIDs não encontradas
	 */
	CIDS_NAO_ENCONTRADAS,
	
	/**
	 * Erro ao desfazer as CID da reapresentacão
	 */
	ERRO_DESFAZER_CID_REAPRESENTACAO,
	
	/**
	 * Itens Conta Hospitalar não encontrada
	 */
	ITENS_CONTA_HOSPITALAR_NAO_ENCONTRADA,
	
	/**
	 * Erro ao desfazer itens da conta reapresentada
	 */
	ERRO_DESFAZER_ITENS_CONTA_REAPRESENTADA,
	
	/**
	 * Conta reapresentada e conta mãe com AIHs diferentes
	 */
	CONTA_REAPRESENTADA_CONTA_MAE_AIH_DIFERENTES,

	/**
	 * AIH não encontrado
	 */
	AIH_NAO_ENCONTRADO,
	
	/**
	 * AIH inválido
	 */
	AIH_INVALIDO,
	
	/**
	 * Não encontrou nro de AIH disponivel
	 */
	NAO_ENCONTROU_AIH_DISPONIVEL,
	
	/**
	 * Atenção: Número de AIH {0} já existente!!
	 */
	NRO_AIH_JA_EXISTENTE,
	
	/**
	 * Erro ao atualizar situação da AIH
	 */
	ERRO_ATUALIZAR_SITUACAI_AIH,
	
	/**
	 * Erro ao desfazer reapresentação
	 */
	ERRO_DESFAZER_REAPRESENTACAO,
	
	/**
	 * Erro ao atualizar saldo de diárias da UTI	
	 */
	ERRO_ATUALIZAR_SALDO_DIARIAS_UTI,
	
	/**
	 * Erro ao atualizar
	 */
	ERRO_AO_ATUALIZAR,
	
	/**
	 * Erro: AIH não associada. {0}
	 */
	ERRO_AIH_NAO_ASSOCIADA,
	
	/**
	 * Erro ao atualizar
	 */
	ERRO_DATA_INICIAL_MENOR_QUE_FINAL,
	
	/**
	 * Erro ao confirmar a transação
	 */
	ERRO_AO_CONFIRMAR_TRANSACAO,
	
	/**
	 * Nao foi possivel estornar: Item de conta APAC ja foi faturado
	 */
	ERRO_ESTORNO_ITEM_CONTA_APAC_VAZIO,

	/**
	 * Nao foi possivel estornar: Item de Procedimento de Ambulatorio ja foi faturado
	 */
	ERRO_ESTORNO_ITEM_PROC_AMB_VAZIO,
	/**
	 * Nao foi possivel encontrar Conta Hospitalar
	 */
	ERRO_CONTA_HOSP_NAO_ENCONTRADA,
	
	/**
	 * Erro ao transformar uma conta fechada em conta sem cobertura
	 */
	ERRO_AO_CONVERTER_CONTA_SEM_COBERTURA,
	
	/**
	 * Erro ao faturar contas de um dia com situação diferente de Fechada
	 */
	ERRO_AO_FATURAR_CONTA_UM_DIA,
	
	/**
	 * Erro ao atualizar dthr_realizado do item de conta hospitalar {1} -RN_CTHC_VER_DATAS
	 */
	ERRO_RN_CTHC_VER_DATAS,

	/**
	 * Situação de Conta hospitalar inválida para reabertura
	 */
	ERRO_VALIDAR_REABERTURA_CH_1,
	
	/**
	 * Erro ao não encontrar uma conta hospitalar válida para reabertura
	 */
	ERRO_VALIDAR_REABERTURA_CH_2,
	/**
	 * Para incluir Auditor SUS o ssm solicitado deve ser informado.
	 */
	AUDITOR_SUS_SEM_SSM,	
	/**
	 * Atenção: Esta não é uma matrícula válida de Auditor SUS.
	 */
	MATRICULA_INVALIDA_AUDITOR_SUS,
	
	/**
	 * Ocorreu algum problema na geração física dos arquivos. Por favor contate o responsável de TI.
	 */
	ARQ_ERRO_FISICO,
	/**
	 * Cadastro da instituição local ou Código CNES (Cadastro Nacional de Estabelecimentos de Saúde) para a instituição não encontrado(s). Verifique o cadastro da instituição local no sistema. 
	 */
	ARQ_SUS_CNES_LOCAL_NAO_ENCONTRADO,
	/**
	 * Existe mais de um cadastro da institui\u00E7\u00E3o local ou C\u00F3digo CNES (Cadastro Nacional de Estabelecimentos de Sa\u00FAde) para a institui\u00E7\u00E3o. Verifique o cadastro da institui\u00E7\u00E3o local no sistema.
	 */
	ARQ_SUS_CNES_LOCAL_NAO_UNICO,
	/**
	 * O valor de pagamento máximo determinado pelo SUS, {1}, foi superado para uma única entrada de espelho de pagamento. Favor revisar esta entrada.
	 */
	ARQ_SUS_TETO_FATURAMENTO_POR_ESPELHO_SUPERADO,
	
	PERIODO_SEM_CONTAS_PARA_ARQUIVO,
	/**
	 * O parametro {1} nao foi corretamente informado.
	 */
	ARQ_SUS_PARAMETRO_INCORRETO,
	/**
	 * Nenhum registro encontrado para o periodo selecionado.
	 */
	ARQ_SUS_NENHUM_REGISTRO,
	/**
	 * Numero de caracteres para registro do tipo {0} diferente do esperado, sao esperados {1} caracteres mas foram obtidos {2} 
	 */
	ARQ_SUS_QTD_CHAR_REG_INVALIDO,
	/**
	 * Numero de registros excedidos, maximo permitido para o tipo {0} eh de {1}, foram informados {2}, registros: {3}
	 */
	ARQ_SUS_QTD_MAX_REG_EXCEDIDO,
	/**
	 * O n\u00FAmero de AIH {0} j\u00E1 existe.
	 */
	ERRO_NUMERO_AIH_JA_INFORMADO,
	
	/**
	 * AIH n\u00E3o pode ser estornada. Existe conta apresentada ou encerrada associada\!
	 */
	ERRO_AIH_NAO_PODE_SER_ESTORNADA,

	/**
	 * Ocorreu um erro ao clonar a conta hospitalar.
	 */
	ERRO_CLONAR_CONTA_HOSPITALAR,

	/**
	 * Erro ao inserir conta internação: {0} - RN_CTHC_ATU_REINT
	 */
	ERRO_INSERIR_CONTA_INTERNACAO,
	
	/**
	 * Erro ao inserir item da conta hospitalar de reinternação: {0} - RN_CTHC_ATU_REINT
	 */
	ERRO_INSERIR_ITEM_CONTA_HOSPITALAR_DE_REINTERNACAO,
	
	/**
	 * Erro ao executar reinternação: {0}
	 */
	ERRO_EXECUTAR_REINTERNACAO,
	
	/**
	 * Conta anterior do paciente já está encerrada. Reinternação não efetuada.
	 */
	ERRO_EXECUTAR_REINTERNACAO_CONTA_ANTERIOR_DO_PACIENTE_JA_ESTA_ENCERRADA,
	
	/**
	 * Iniciais inválidas (Usar A-Z separados por ,)
	 */
	INICIAS_RELATORIO_PEND_ENC_INVALIDAS,
	
	/**
	 * Conta {0} não teve reinternação.
	 */
	ERRO_EXECUTAR_REINTERNACAO_CONTA_NAO_TEVE_REINTERNACAO,

	/**
	 * Competência inválida.
	 */
	COMPETENCIA_INVALIDA,
	
	/**
	 * Erro ao desfazer reapresentação: {0}
	 */
	MENSAGEM_ERRO_DESFAZER_REAPRESENTACAO,
	
	/**
	 * Conta reapresentada não pode ser desfeita
	 */
	MENSAGEM_CONTA_REAPRESENTADA_NAO_PODE_SER_DESFEITA,
	
	/**
	 * Informe o Motivo de Rejeição da Conta Hospitalar
	 */
	ERRO_INFORME_MOTIVO_REJEICAO_CONTA_HOSPITALAR,
	
	/**
	 * Erro ao atualizar uti.
	 */
	MENSAGEM_ERRO_ATUALIZAR_UTI,
	
	/**
	 * Erro ao reapresentar: {0}
	 */
	MENSAGEM_ERRO_REAPRESENTAR,
	
	/**
	 * Conta Hospitalar não pode ser reapresentada.
	 */
	MENSAGEM_CONTA_HOSPITALAR_NAO_PODE_SER_REAPRESENTADA,
	
	/**
	 * DCIH não pode ser reapresentada.
	 */
	MENSAGEM_DCIH_NAO_PODE_SER_REAPRESENTADA,
	
	/**
	 * DCIH reapresentada com sucesso.
	 */
	MENSAGEM_DCIH_REAPRESENTADA_SUCESSO,
	
	/**
	 * Erro na finalização da  reapresentação {0}
	 */
	MENSAGEM_ERRO_FINALIZACAO_REAPRESENTACAO,
	
	/**
	 * Erro ao gerar conta para reapresentação {0}
	 */
	MENSAGEM_ERRO_GERAR_CONTA_REAPRESENTACAO,
	
	/**
	 * Erro ao gerar CID para reapresentação {0}
	 */
	MENSAGEM_ERRO_GERAR_CID_REAPRESENTACAO,
	
	/**
	 * Erro ao gerar internação para reapresentação {0}
	 */
	MENSAGEM_ERRO_GERAR_INTERNACAO_REAPRESENTACAO,
	
	/**
	 * Erro ao gerar item da conta para reapresentação {0}
	 */
	MENSAGEM_ERRO_GERAR_ITEM_CONTA_REAPRESENTACAO,
	
	CAMPOS_PESQUISA_CTH_PARA_COBRANCA_SEM_INTERNACAO_NAO_INFORMADOS,
	
	/**
	 * Erro ao executar sugestao desdobramento: {0}
	 */
	MENSAGEM_ERRO_EXECUTAR_SUGESTAO_DESDOBRAMENTO,
	
	/**
	 * Erro ao inserir Sugestão de Desdobramento {0}
	 */
	ERRO_INSERIR_SUGESTAO_DESDOBRAMENTO,
	
	/**
	 * Alteração não permitida. Conta faturada.
	 */
	MENSAGEM_ERRO_UPDATE_FAT_DADOS_CONTA_SEM_INTERNACAO,
	
	ERRO_DATA_FINAL_ANTERIOR_A_INICIAL_FAT_DADOS_CONTA_SEM_INTERNACAO,
	
	/**
	 * Progresso cancelado pelo usuário.
	 */
	ERRO_PROGRESSO_CANCELADO_PELO_USUARIO,
	
	QUANTIDADE_TETO_VALOR_IGUAL_A_ZERO,
	
	TETO_VALOR_IGUAL_A_ZERO
	
	;
	
	public void throwException(Object... params) throws ApplicationBusinessException {

		throw new ApplicationBusinessException(this, params);
	}

	public void throwException(Throwable cause, Object... params) throws ApplicationBusinessException {

		// Tratamento adicional para não esconder a excecao de negocio
		// original
		if (cause instanceof ApplicationBusinessException) {
			throw (ApplicationBusinessException) cause;
		}
		throw new ApplicationBusinessException(this, cause, params);
	}
}

