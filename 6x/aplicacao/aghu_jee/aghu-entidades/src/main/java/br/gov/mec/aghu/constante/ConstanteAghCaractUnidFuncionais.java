package br.gov.mec.aghu.constante;

import br.gov.mec.aghu.core.dominio.DominioString;


/*
 * Dados obtidos a partir de:
 *
 *  select RV_LOW_VALUE, RV_MEANING 
 *  from AGH_REF_CODES
 *  where  RV_DOMAIN = 'CARACT_UNID_FUNCIONAL' 
 *  order by  1 
 *  
 */
@SuppressWarnings({"PMD.CyclomaticComplexity"})
public enum ConstanteAghCaractUnidFuncionais implements DominioString {

	ALERTA_ATENDER_LEITO_TRANSF,
	ANAMNESE_EVOLUCAO_ELETRONICA, 
	APLICACAO_QUIMIO_INTRATECAL, 
	AREA_FECHADA, 
	AREA_FECHADA_BANCO_DE_SANGUE, 
	ATEND_EMERG_TERREO, 
	AUTOMACAO_ROTINA, 
	AVISA_EXAME_REALIZADO,
	IMPRIME_BA_EMERGENCIA,
	BLOCO, 
	BLOQ_LTO_ISOLAMENTO,
	CALC_VOL_NPT_ADICIONAL,
	CCA, 
	CENTRAL_RECEBIMENTO_MATERIAIS, 
	CHECAGEM_ELETRONICA, 
	CHECAGEM_ELETRONICA_ENFERMAGEM, 
	CHEFIA_ASS_ELET, 
	CID_OPCIONAL_ATEND_URGENCIA, 
	CO, 
	COLETA_REALIZADA_UNIDADE, 
	COMPRESSAO_IMPRESSAO_LAUDO, 
	CONS_CLIN, 
	CONTROLA_PENDENCIA_NA, 
	CONTROLA_TRAT_AMBULATORIAL, 
	CONTROLA_UNID_PAI,
	CONTROLA_PREVISAO_ALTA,
	CRITICA_APAC_SISTEMA, 
	DEFAULT_DE_EXAME_URGENTE, 
	DIARIA_UTI_1, 
	DIARIA_UTI_2, 
	DIARIA_UTI_3, 
	DIETA_OPCIONAL_ATEND_URGENCIA, 
	EMERGENCIA_OBSTETRICA, 
	EMITE_ROTULO_NUTR_NEONATOLOGIA, 
	EMITE_ROTULO_NUTR_PEDIATRICA, 
	EXAME_SISMAMA_CITO, 
	EXAME_SISMAMA_HISTO, 
	EXAME_SISMAMA_MAMO, 
	FATURA_SERVICO_PROFISSIONAL, 
	GERA_NRO_UNICO, 
	HEMODIALISE, 
	HEMODINAMICA, 
	IMPRIME_ETIQUETAS_CARACTER,
	IMPRIME_EXAMES_SEM_QUEBRA_PAG,
	IMPRIME_FICHA_POR_AMOSTRA, 
	IMPRIME_FICHA_POR_EXAME, 
	IMPRIME_NOME_CHEFIA, 
	IMPRIME_NOME_PACIENTE, 
	IMPRIME_TICKET_FARMACIA, 
	LAUDO_ACOMP, 
	LAUDO_CTI, 
	NAO_APRAZA_CUIDADO_PEN, 
	NAO_APRAZA_CUIDADO_PME, 
	NAO_APRAZA_MDTO_PME, 
	NAO_FATURAR_CONVENIOS, 
	NAO_OBTEM_DILUICAO, 
	OBTEM_DILUICAO, 
	PATOLOGIA_CLINICA, 
	PEN_CONSECUTIVA, 
	PEN_INFORMATIZADA,
	PERMITE_AGRUPAR_EXAMES,
	PERMITE_ATOS_ANESTESICOS, 
	PERMITE_INF_PRESCRIBENTE, 
	PERMITE_NOTIFICAR_MCI_PACIENTE, 
	PERMITE_PACIENTE_EXTRA,
	PERMITE_PRESCRICAO_BI,
	PERMITE_SUMARIO_ALTA_MANUAL, 
	PME_CONSECUTIVA, 
	PME_INFORMATIZADA, 
	POSSUI_IMPRESSORA_PADRAO, 
	POSSUI_QRTO_EXCLUSIV_INFEC, 
	PROTOCOLA_PACIENTE, 
	REC_HUMANOS,	
	REGISTRO_ATEND_AMB_PARA_EXAMES,
	SALA_GESSO,
	SALA_RECUPERACAO, 
	SMO, 
	SOLICITA_EXAMES_PELO_SISTEMA, 
	SOLICITA_RESPONSAVEL, 
	TICKET_EXAME_PAC_EXTERNO, 
	UNID_AMBULATORIO, 
	UNID_APLICACAO_QUIMIOTERAPIA, 
	UNID_BANCO_SANGUE, 
	UNID_BIOQUIMICA, 
	UNID_COLETA, 
	UNID_CONVENIO, 
	UNID_CTI, 
	UNID_CTI_POS_OPER_CARD, 
	UNID_ECOGRAFIA, 
	UNID_EMERGENCIA, 
	UNID_EXECUTORA_CIRURGIAS, 
	UNID_EXECUTORA_EXAMES, 
	UNID_FARMACIA, 
	UNID_FISIATRIA, 
	UNID_GENETICA, 
	UNID_HEMATOLOGIA, 
	UNID_HOSP_DIA, 
	UNID_IMUNOLOGIA, 
	UNID_INTERNACAO, 
	UNID_MED_NUCLEAR, 
	UNID_MICROBIOLOGIA, 
	UNID_NEONATOLOGIA, 
	UNID_OBSTETRICA,
	UNID_OFTALMO,
	UNID_OTORRINO, 
	UNID_PATOLOGIA, 
	UNID_PESQUISA, 
	UNID_PNEUMOLOGIA, 
	UNID_PSIQUIATRICA, 
	UNID_RADIODIAGNOSTICO, 
	UNID_RADIOIMUNOENSAIO, 
	UNID_RADIOLOGIA, 
	UNID_SATELITE_IG, 
	UNID_SOROLOGIA_DOADORES, 
	UNID_URGENCIA, 
	UNID_UTIN, 
	UNID_UTIP, 
	UNID_ZONA_14, 
	USA_NOVO_LAUDO, 
	USA_SELO_LAUDO, 
	VERF_ESCALA_PROF_INT, 
	ZONA_AMBULATORIO,
	CONTROLES_PACIENTE_INFORMATIZADO, //adicionado estoria 6950
	INFORMA_ESTADO_SAUDE,	
	UNID_FARMACIA_DISPENSARIO,
	INTEGRACAO_DISPENSARIO_SEM_AVAL_FARM,
	UBS,
	IMPRIME_ETIQUETAS_AMOSTRA,
	IMPRIME_PRESCRICAO_FARMACIA;

	@Override
	@SuppressWarnings({"PMD.NcssMethodCount"})
	public String getCodigo() {
		switch (this) {
		case ANAMNESE_EVOLUCAO_ELETRONICA:
			return "Anamnese/Evolução Eletrônica";
		case APLICACAO_QUIMIO_INTRATECAL:
			return "Aplicacao Quimio Intratecal";
		case AREA_FECHADA:
			return "Area Fechada";
		case AREA_FECHADA_BANCO_DE_SANGUE:
			return "Area Fechada Banco de Sangue";
		case ATEND_EMERG_TERREO:
			return "Atend emerg terreo";
		case AUTOMACAO_ROTINA:
			return "Automacao Rotina";
		case AVISA_EXAME_REALIZADO:
			return "Avisa Exame Realizado";
		case BLOCO:
			return "Bloco";
		case BLOQ_LTO_ISOLAMENTO:
			return "Bloq Lto Isolamento";
		case CALC_VOL_NPT_ADICIONAL:
			return "Calc Vol NPT Adicional";
		case CCA:
			return "CCA";
		case CENTRAL_RECEBIMENTO_MATERIAIS:
			return "Central Recebimento Materiais";
		case CHECAGEM_ELETRONICA:
			return "Checagem eletronica";
		case CHECAGEM_ELETRONICA_ENFERMAGEM:
			return "Checagem eletronica enfermagem";
		case CHEFIA_ASS_ELET:
			return "Chefia Ass Elet";
		case CID_OPCIONAL_ATEND_URGENCIA:
			return "Cid opcional atend urgencia";
		case CO:
			return "CO";
		case COLETA_REALIZADA_UNIDADE:
			return "Coleta Realizada Unidade";
		case COMPRESSAO_IMPRESSAO_LAUDO:
			return "Compressao Impressao Laudo";
		case CONS_CLIN:
			return "Cons Clin";
		case CONTROLA_PENDENCIA_NA:
			return "Controla Pendencia NA";
		case CONTROLA_TRAT_AMBULATORIAL:
			return "Controla Trat Ambulatorial";
		case CONTROLA_UNID_PAI:
			return "Controla Unid Pai";
		case CRITICA_APAC_SISTEMA:
			return "Critica APAC sistema";
		case CONTROLA_PREVISAO_ALTA:
			return "Control prev alta";
		case DEFAULT_DE_EXAME_URGENTE:
			return "Default de Exame Urgente";
		case DIARIA_UTI_1:
			return "Diaria UTI 1";
		case DIARIA_UTI_2:
			return "Diaria UTI 2";
		case DIARIA_UTI_3:
			return "Diaria UTI 3";
		case DIETA_OPCIONAL_ATEND_URGENCIA:
			return "Dieta opcional atend urgencia";
		case EMERGENCIA_OBSTETRICA:
			return "Emergencia Obstetrica";
		case EMITE_ROTULO_NUTR_NEONATOLOGIA:
			return "Emite Rótulo Nutr Neonatologia";
		case EMITE_ROTULO_NUTR_PEDIATRICA:
			return "Emite Rótulo Nutr Pediatrica";
		case EXAME_SISMAMA_CITO:
			return "Exame SISMAMA Cito";
		case EXAME_SISMAMA_HISTO:
			return "Exame SISMAMA Histo";
		case EXAME_SISMAMA_MAMO:
			return "Exame SISMAMA Mamo";
		case FATURA_SERVICO_PROFISSIONAL:
			return "Fatura Servico Profissional";
		case GERA_NRO_UNICO:
			return "Gera Nro Unico";
		case HEMODIALISE:
			return "Hemodialise";
		case HEMODINAMICA:
			return "Hemodinamica";
		case IMPRIME_BA_EMERGENCIA:
			return "Imprime BA Emergencia";	
		case IMPRIME_ETIQUETAS_CARACTER:
			return "Imprime Etiquetas Caracter";
		case IMPRIME_EXAMES_SEM_QUEBRA_PAG:
			return "Imprime Exames sem Quebra Pag";
		case IMPRIME_FICHA_POR_AMOSTRA:
			return "Imprime Ficha por Amostra";
		case IMPRIME_FICHA_POR_EXAME:
			return "Imprime Ficha por Exame";
		case IMPRIME_NOME_CHEFIA:
			return "Imprime Nome Chefia";
		case IMPRIME_NOME_PACIENTE:
			return "Imprime Nome Paciente";
		case IMPRIME_TICKET_FARMACIA:
			return "Imprime Ticket Farmacia";
		case LAUDO_ACOMP:
			return "Laudo Acomp";
		case LAUDO_CTI:
			return "Laudo CTI";
		case NAO_APRAZA_CUIDADO_PEN:
			return "Nao Apraza Cuidado Pen";
		case NAO_APRAZA_CUIDADO_PME:
			return "Nao Apraza Cuidado Pme";
		case NAO_APRAZA_MDTO_PME:
			return "Nao Apraza Mdto Pme";
		case NAO_FATURAR_CONVENIOS:
			return "Não faturar convênios";
		case NAO_OBTEM_DILUICAO:
			return "Nao Obtem Diluicao";
		case OBTEM_DILUICAO:
			return "Obtem Diluicao";
		case PATOLOGIA_CLINICA:
			return "Patologia Clinica";
		case PEN_CONSECUTIVA:
			return "Pen Consecutiva";
		case PEN_INFORMATIZADA:
			return "Pen Informatizada";
		case PERMITE_AGRUPAR_EXAMES:
			return "Permite Agrupar Exames";
		case PERMITE_ATOS_ANESTESICOS:
			return "Permite atos anestésicos";
		case PERMITE_INF_PRESCRIBENTE:
			return "Permite Inf Prescribente";
		case PERMITE_NOTIFICAR_MCI_PACIENTE:
			return "Permite Notificar MCI Paciente";
		case PERMITE_PACIENTE_EXTRA:
			return "Permite Paciente Extra";
		case PERMITE_PRESCRICAO_BI:
			return "Permite Prescrição BI";
		case PERMITE_SUMARIO_ALTA_MANUAL:
			return "Permite Sumario Alta Manual";
		case PME_CONSECUTIVA:
			return "Pme Consecutiva";
		case PME_INFORMATIZADA:
			return "Pme Informatizada";
		case POSSUI_IMPRESSORA_PADRAO:
			return "Possui Impressora Padrao";
		case POSSUI_QRTO_EXCLUSIV_INFEC:
			return "Possui Qrto Exclusiv Infec";
		case PROTOCOLA_PACIENTE:
			return "Protocola Paciente";
		case REC_HUMANOS:	
			return "Rec Humanos";
		case REGISTRO_ATEND_AMB_PARA_EXAMES:
			return "Registro Atend Amb p/ Exames";
		case SALA_GESSO:
			return "Sala Gesso";
		case SALA_RECUPERACAO:
			return "Sala Recuperacao";
		case SMO:
			return "SMO";
		case SOLICITA_EXAMES_PELO_SISTEMA:
			return "Solicita Exames pelo Sistema";
		case SOLICITA_RESPONSAVEL:
			return "Solicita responsavel";
		case TICKET_EXAME_PAC_EXTERNO:
			return "Ticket Exame Pac Externo";
		case UNID_AMBULATORIO:
			return "Unid Ambulatorio";
		case UNID_APLICACAO_QUIMIOTERAPIA:
			return "Unid Aplicacao Quimioterapia";
		case UNID_BANCO_SANGUE:
			return "Unid Banco Sangue";
		case UNID_BIOQUIMICA:
			return "Unid Bioquimica";
		case UNID_COLETA:
			return "Unid Coleta";
		case UNID_CONVENIO:
			return "Unid Convenio";
		case UNID_CTI:
			return "Unid CTI";
		case UNID_CTI_POS_OPER_CARD:
			return "Unid CTI Pos Oper Card";
		case UNID_ECOGRAFIA:
			return "Unid Ecografia";
		case UNID_EMERGENCIA:
			return "Unid Emergencia";
		case UNID_EXECUTORA_CIRURGIAS:
			return "Unid Executora Cirurgias";
		case UNID_EXECUTORA_EXAMES:
			return "Unid Executora Exames";
		case UNID_FARMACIA:
			return "Unid Farmacia";
		case UNID_FISIATRIA:
			return "Unid Fisiatria";
		case UNID_GENETICA:
			return "Unid Genetica";
		case UNID_HEMATOLOGIA:
			return "Unid Hematologia";
		case UNID_HOSP_DIA:
			return "Unid Hosp Dia";
		case UNID_IMUNOLOGIA:
			return "Unid Imunologia";
		case UNID_INTERNACAO:
			return "Unid Internacao";
		case UNID_MED_NUCLEAR:
			return "Unid Med Nuclear";
		case UNID_MICROBIOLOGIA:
			return "Unid Microbiologia";
		case UNID_NEONATOLOGIA:
			return "Unid Neonatologia";
		case UNID_OBSTETRICA:
			return "Unid Obstetrica";
		case UNID_OFTALMO:
			return "Unid Oftalmo";
		case UNID_OTORRINO:
			return "Unid Otorrino";
		case UNID_PATOLOGIA:
			return "Unid Patologia";
		case UNID_PESQUISA:
			return "Unid Pesquisa";
		case UNID_PNEUMOLOGIA:
			return "Unid Pneumologia";
		case UNID_PSIQUIATRICA:
			return "Unid Psiquiatrica";
		case UNID_RADIODIAGNOSTICO:
			return "Unid Radiodiagnostico";
		case UNID_RADIOIMUNOENSAIO:
			return "Unid Radioimunoensaio";
		case UNID_RADIOLOGIA:
			return "Unid Radiologia";
		case UNID_SATELITE_IG:
			return "Unid Satélite - IG";
		case UNID_SOROLOGIA_DOADORES:
			return "Unid sorologia doadores";
		case UNID_URGENCIA:
			return "Unid Urgencia";
		case UNID_UTIN:
			return "Unid UTIN";
		case UNID_UTIP:
			return "Unid UTIP";
		case UNID_ZONA_14:
			return "Unid Zona 14";
		case USA_NOVO_LAUDO:
			return "Usa Novo Laudo";
		case USA_SELO_LAUDO:
			return "Usa Selo Laudo";
		case VERF_ESCALA_PROF_INT:
			return "Verf Escala Prof Int";
		case ZONA_AMBULATORIO:
			return "Zona Ambulatorio";
		case ALERTA_ATENDER_LEITO_TRANSF:
			return "Alerta atender leito transf";
		case CONTROLES_PACIENTE_INFORMATIZADO:
			return "Controles Pac Informatizado";
		case INFORMA_ESTADO_SAUDE:
			return "Informa Estado Saúde";
		case UNID_FARMACIA_DISPENSARIO:
			return "Unid Farmacia Dispensario";
		case INTEGRACAO_DISPENSARIO_SEM_AVAL_FARM:
			return "Integracao Dispensario sem Ava";		
		case UBS:
			return "Unidade Básica de Saúde";
		case IMPRIME_ETIQUETAS_AMOSTRA:
			return "Imprime Etiquetas Amostra";
		case IMPRIME_PRESCRICAO_FARMACIA:
			return "Imprime prescricao farmacia";
		default:
			return "";
		}
	}

	@Override
	@SuppressWarnings({"PMD.NcssMethodCount"})
	public String getDescricao() {
		switch (this) {
		case ANAMNESE_EVOLUCAO_ELETRONICA:
			return "Anamnese/Evolução Eletrônica";
		case APLICACAO_QUIMIO_INTRATECAL:
			return "Unidade Aplicação Quimioterapia Intra Tecal";
		case AREA_FECHADA:
			return "Area Fechada";
		case AREA_FECHADA_BANCO_DE_SANGUE:
			return "Area Fechada para Banco de Sangue";
		case ATEND_EMERG_TERREO:
			return "Unidade atendimento Emergência Térreo";
		case AUTOMACAO_ROTINA:
			return "Unidade de rotina para automação";
		case AVISA_EXAME_REALIZADO:
			return "Avisa Exame Realizado";
		case BLOCO:
			return "Unidade de Bloco Cirúrgico";
		case BLOQ_LTO_ISOLAMENTO:
			return "Bloqueia Leito Isolamento";
		case CALC_VOL_NPT_ADICIONAL:
			return "Calcula Volume NPT Adicional";
		case CCA:
			return "Unidade de CCA";
		case CENTRAL_RECEBIMENTO_MATERIAIS:
			return "Central de Recebimento de Materiais";
		case CHECAGEM_ELETRONICA:
			return "Checagem eletronica";
		case CHECAGEM_ELETRONICA_ENFERMAGEM:
			return "Checagem eletronica enfermagem";
		case CHEFIA_ASS_ELET:
			return "Chefia Assinatura Eletrônica";
		case CID_OPCIONAL_ATEND_URGENCIA:
			return "Cid opcional atend urgencia";
		case CO:
			return "Unidade de CO";
		case COLETA_REALIZADA_UNIDADE:
			return "Coleta Realizada pela Unidade Solicitante";
		case COMPRESSAO_IMPRESSAO_LAUDO:
			return "Compressao Impressao Laudo";
		case CONS_CLIN:
			return "Consiste Clínica";
		case CONTROLA_PENDENCIA_NA:
			return "Controla pendência de nível assistencial";
		case CONTROLA_TRAT_AMBULATORIAL:
			return "Controla Trat Ambulatorial";
		case CONTROLA_UNID_PAI:
			return "Controla Unid. Pai para sistema de exames";
		case CRITICA_APAC_SISTEMA:
			return "Critica APAC sistema";
		case CONTROLA_PREVISAO_ALTA:
			return "Controla previsão alta";
		case DEFAULT_DE_EXAME_URGENTE:
			return "Default de Exame Urgente";
		case DIARIA_UTI_1:
			return "Diária UTI 1";
		case DIARIA_UTI_2:
			return "Diária UTI 2";
		case DIARIA_UTI_3:
			return "Diária UTI 3";
		case DIETA_OPCIONAL_ATEND_URGENCIA:
			return "Dieta opcional atend urgencia";
		case EMERGENCIA_OBSTETRICA:
			return "Emergência Obstétrica";
		case EMITE_ROTULO_NUTR_NEONATOLOGIA:
			return "Emite Rótulo Sup. Nutricional Neonatologia";
		case EMITE_ROTULO_NUTR_PEDIATRICA:
			return "Emite Rótulo Sup. Nutricional Pediátrico";
		case EXAME_SISMAMA_CITO:
			return "Exame SISMAMA Cito";
		case EXAME_SISMAMA_HISTO:
			return "Exame SISMAMA Histo";
		case EXAME_SISMAMA_MAMO:
			return "Exame SISMAMA Mamo";
		case FATURA_SERVICO_PROFISSIONAL:
			return "Fatura Servico Profissional";
		case GERA_NRO_UNICO:
			return "Gera Numero Unico";
		case HEMODIALISE:
			return "Unidade de Hemodiálise";
		case HEMODINAMICA:
			return "Unidade de Hemodinamica";
		case IMPRIME_BA_EMERGENCIA:
			return "Imprime BA Emergencia";		
		case IMPRIME_ETIQUETAS_CARACTER:
			return "Imprime Etiquetas Modo Caracter";
		case IMPRIME_EXAMES_SEM_QUEBRA_PAG:
			return "Imprime exames sem quebrar pagina";
		case IMPRIME_FICHA_POR_AMOSTRA:
			return "Imprime Ficha por Amostra";
		case IMPRIME_FICHA_POR_EXAME:
			return "Imprime Ficha por Exame";
		case IMPRIME_NOME_CHEFIA:
			return "Imprime Nome Chefia em Laudos";
		case IMPRIME_NOME_PACIENTE:
			return "Imprime Nome do Paciente no Fluxograma";
		case IMPRIME_TICKET_FARMACIA:
			return "Imprime Ticket Farmácia";
		case LAUDO_ACOMP:
			return "Laudo acompanhante exigido";
		case LAUDO_CTI:
			return "Laudo CTI exigido";
		case NAO_APRAZA_CUIDADO_PEN:
			return "Não Apraza Cuidado Presc. Enfermagem";
		case NAO_APRAZA_CUIDADO_PME:
			return "Não Apraza Cuidado Presc. Médica";
		case NAO_APRAZA_MDTO_PME:
			return "Não Apraza Medicamento Presc. Médica";
		case NAO_FATURAR_CONVENIOS:
			return "Não faturar convênios";
		case NAO_OBTEM_DILUICAO:
			return "Nao Obtem Diluicao";
		case OBTEM_DILUICAO:
			return "Obtem Diluicao";
		case PATOLOGIA_CLINICA:
			return "Patologia Clinica";
		case PEN_CONSECUTIVA:
			return "Prescrição Enfermagem Consecutiva";
		case PEN_INFORMATIZADA:
			return "Prescrição Enfermagem Informatizada";
		case PERMITE_AGRUPAR_EXAMES:
			return "Permite Agrupar Exames";
		case PERMITE_ATOS_ANESTESICOS:
			return "Permite atos anestésicos";
		case PERMITE_INF_PRESCRIBENTE:
			return "Permite Enviar Informação Prescribente";
		case PERMITE_NOTIFICAR_MCI_PACIENTE:
			return "Permite Notificar MCI Paciente";
		case PERMITE_PACIENTE_EXTRA:
			return "Permite Paciente Extra";
		case PERMITE_PRESCRICAO_BI:
			return "Permite Prescrição BI";
		case PERMITE_SUMARIO_ALTA_MANUAL:
			return "Permite Sumario Alta Manual";
		case PME_CONSECUTIVA:
			return "Prescrição Médica Consecutiva";
		case PME_INFORMATIZADA:
			return "Prescrição Médica Informatizada";
		case POSSUI_IMPRESSORA_PADRAO:
			return "Possui Impressora Padrao";
		case POSSUI_QRTO_EXCLUSIV_INFEC:
			return "Possui Quarto Exclusivo Infecção";
		case PROTOCOLA_PACIENTE:
			return "Protocola Paciente";
		case REC_HUMANOS:	
			return "Unidade de Recursos Humanos";
		case REGISTRO_ATEND_AMB_PARA_EXAMES:
			return "Controle entrada/saida pac. ambulatório";
		case SALA_GESSO:
			return "Unidade Sala de Gesso";
		case SALA_RECUPERACAO:
			return "Unidade de Sala de Recuperacao";
		case SMO:
			return "Unidade de SMO";
		case SOLICITA_EXAMES_PELO_SISTEMA:
			return "Solicita Exames pelo Sistema";
		case SOLICITA_RESPONSAVEL:
			return "Solicita responsável";
		case TICKET_EXAME_PAC_EXTERNO:
			return "Ticket Exame Pac Externo";
		case UNID_AMBULATORIO:
			return "Unidade de Ambulatorio";
		case UNID_APLICACAO_QUIMIOTERAPIA:
			return "Unidade Aplicação de Quimioterapia";
		case UNID_BANCO_SANGUE:
			return "Unidade Banco de Sangue";
		case UNID_BIOQUIMICA:
			return "Unidade Bioquímica";
		case UNID_COLETA:
			return "Unidade de Coleta";
		case UNID_CONVENIO:
			return "Unidade de Convenios";
		case UNID_CTI:
			return "Unidade CTI";
		case UNID_CTI_POS_OPER_CARD:
			return "CTI Pós Operatório Cir. Cardíaca";
		case UNID_ECOGRAFIA:
			return "Unidade de Ecografia";
		case UNID_EMERGENCIA:
			return "Unidade de Emergencia";	
		case UNID_EXECUTORA_CIRURGIAS:
			return "Unidade Executora de Cirurgias";
		case UNID_EXECUTORA_EXAMES:
			return "Unidade Executora de Exames";
		case UNID_FARMACIA:
			return "Unidade de Farmácia";
		case UNID_FISIATRIA:
			return "Unidade Fisiatria";
		case UNID_GENETICA:
			return "Unidade de Genética";
		case UNID_HEMATOLOGIA:
			return "Unidade Hematologia";
		case UNID_HOSP_DIA:
			return "Unidade do Hospital Dia";
		case UNID_IMUNOLOGIA:
			return "Unidade Imunologia";
		case UNID_INTERNACAO:
			return "Unidade de Internação";
		case UNID_MED_NUCLEAR:
			return "Unidade Medicina Nuclear";
		case UNID_MICROBIOLOGIA:
			return "Unidade Microbiologia";
		case UNID_NEONATOLOGIA:
			return "Unidade de Neonatologia";
		case UNID_OBSTETRICA:
			return "Unidade Obstétrica";
		case UNID_OFTALMO:
			return "Unidade de Oftalmo";
		case UNID_OTORRINO:
			return "Unidade de Otorrino";
		case UNID_PATOLOGIA:
			return "Unidade Patologia";
		case UNID_PESQUISA:
			return "Unidade de Pesquisa";
		case UNID_PNEUMOLOGIA:
			return "Unidade Pneumologia";
		case UNID_PSIQUIATRICA:
			return "Unid Psiquiatrica";
		case UNID_RADIODIAGNOSTICO:
			return "Unidade Radiodiagnóstico";
		case UNID_RADIOIMUNOENSAIO:
			return "Unidade Radioimunoensaio";
		case UNID_RADIOLOGIA:
			return "Unidade Radiologia";
		case UNID_SATELITE_IG:
			return "Unidade Satélite - IG";
		case UNID_SOROLOGIA_DOADORES:
			return "Unidade de sorologia de doadores";
		case UNID_URGENCIA:
			return "Unidade de Urgência";
		case UNID_UTIN:
			return "Unidade UTIN";
		case UNID_UTIP:
			return "Unidade UTIP";
		case UNID_ZONA_14:
			return "Unidade de Coleta Zona 14";
		case USA_NOVO_LAUDO:
			return "Usa novo formato de Impressão de Laudo";
		case USA_SELO_LAUDO:
			return "Usa Selo de Acreditação no Laudo";
		case VERF_ESCALA_PROF_INT:
			return "Verifica Escala Profissional Internação";
		case ZONA_AMBULATORIO:
			return "Zona Ambulatorio";
		case ALERTA_ATENDER_LEITO_TRANSF:
			return "Alerta p/ atender leito transferência";
		case CONTROLES_PACIENTE_INFORMATIZADO:
			return "Controles do Paciente Informatizado";
		case INFORMA_ESTADO_SAUDE:
			return "Informa Estado de Saúde";
		case UNID_FARMACIA_DISPENSARIO:
			return "Unid Farmacia Dispensario";
		case INTEGRACAO_DISPENSARIO_SEM_AVAL_FARM:
			return "Integracao Dispensario sem Aval Farm";
		case UBS:
			return "Unidade Básica de Saúde";
		case IMPRIME_ETIQUETAS_AMOSTRA:
			return "Imprime Etiquetas Amostra";
		case IMPRIME_PRESCRICAO_FARMACIA:
			return "Imprime prescricao farmacia";
		default:
			return "";
		}
	}

	@SuppressWarnings({"PMD.NcssMethodCount"})
	public static ConstanteAghCaractUnidFuncionais getEnum(String enumString) {
		switch (enumString) {
		case "ANAMNESE_EVOLUCAO_ELETRONICA":
			return ANAMNESE_EVOLUCAO_ELETRONICA;
		case "APLICACAO_QUIMIO_INTRATECAL":
			return APLICACAO_QUIMIO_INTRATECAL;
		case "AREA_FECHADA":
			return AREA_FECHADA;
		case "AREA_FECHADA_BANCO_DE_SANGUE":
			return AREA_FECHADA_BANCO_DE_SANGUE;
		case "ATEND_EMERG_TERREO":
			return ATEND_EMERG_TERREO;
		case "AUTOMACAO_ROTINA":
			return AUTOMACAO_ROTINA;
		case "AVISA_EXAME_REALIZADO":
			return AVISA_EXAME_REALIZADO;
		case "BLOCO":
			return BLOCO;
		case "BLOQ_LTO_ISOLAMENTO":
			return BLOQ_LTO_ISOLAMENTO;
		case "CALC_VOL_NPT_ADICIONAL":
			return CALC_VOL_NPT_ADICIONAL;
		case "CCA":
			return CCA;
		case "CENTRAL_RECEBIMENTO_MATERIAIS":
			return CENTRAL_RECEBIMENTO_MATERIAIS;
		case "CHECAGEM_ELETRONICA":
			return CHECAGEM_ELETRONICA;
		case "CHECAGEM_ELETRONICA_ENFERMAGEM":
			return CHECAGEM_ELETRONICA_ENFERMAGEM;
		case "CHEFIA_ASS_ELET":
			return CHEFIA_ASS_ELET;
		case "CID_OPCIONAL_ATEND_URGENCIA":
			return CID_OPCIONAL_ATEND_URGENCIA;
		case "CO":
			return CO;
		case "COLETA_REALIZADA_UNIDADE":
			return COLETA_REALIZADA_UNIDADE;
		case "COMPRESSAO_IMPRESSAO_LAUDO":
			return COMPRESSAO_IMPRESSAO_LAUDO;
		case "CONS_CLIN":
			return CONS_CLIN;
		case "CONTROLA_PENDENCIA_NA":
			return CONTROLA_PENDENCIA_NA;
		case "CONTROLA_TRAT_AMBULATORIAL":
			return CONTROLA_TRAT_AMBULATORIAL;
		case "CONTROLA_UNID_PAI":
			return CONTROLA_UNID_PAI;
		case "CRITICA_APAC_SISTEMA":
			return CRITICA_APAC_SISTEMA;
		case "CONTROLA_PREVISAO_ALTA":
			return CONTROLA_PREVISAO_ALTA;
		case "DEFAULT_DE_EXAME_URGENTE":
			return DEFAULT_DE_EXAME_URGENTE;
		case "DIARIA_UTI_1":
			return DIARIA_UTI_1;
		case "DIARIA_UTI_2":
			return DIARIA_UTI_2;
		case "DIARIA_UTI_3":
			return DIARIA_UTI_3;
		case "DIETA_OPCIONAL_ATEND_URGENCIA":
			return DIETA_OPCIONAL_ATEND_URGENCIA;
		case "EMERGENCIA_OBSTETRICA":
			return EMERGENCIA_OBSTETRICA;
		case "EMITE_ROTULO_NUTR_NEONATOLOGIA":
			return EMITE_ROTULO_NUTR_NEONATOLOGIA;
		case "EMITE_ROTULO_NUTR_PEDIATRICA":
			return EMITE_ROTULO_NUTR_PEDIATRICA;
		case "EXAME_SISMAMA_CITO":
			return EXAME_SISMAMA_CITO;
		case "EXAME_SISMAMA_HISTO":
			return EXAME_SISMAMA_HISTO;
		case "EXAME_SISMAMA_MAMO":
			return EXAME_SISMAMA_MAMO;
		case "FATURA_SERVICO_PROFISSIONAL":
			return FATURA_SERVICO_PROFISSIONAL;
		case "GERA_NRO_UNICO":
			return GERA_NRO_UNICO;
		case "HEMODIALISE":
			return HEMODIALISE;
		case "HEMODINAMICA":
			return HEMODINAMICA;
		case "IMPRIME_ETIQUETAS_CARACTER":
			return IMPRIME_ETIQUETAS_CARACTER;
		case "IMPRIME_EXAMES_SEM_QUEBRA_PAG":
			return IMPRIME_EXAMES_SEM_QUEBRA_PAG;
		case "IMPRIME_FICHA_POR_AMOSTRA":
			return IMPRIME_FICHA_POR_AMOSTRA;
		case "IMPRIME_FICHA_POR_EXAME":
			return IMPRIME_FICHA_POR_EXAME;
		case "IMPRIME_NOME_CHEFIA":
			return IMPRIME_NOME_CHEFIA;
		case "IMPRIME_NOME_PACIENTE":
			return IMPRIME_NOME_PACIENTE;
		case "IMPRIME_TICKET_FARMACIA":
			return IMPRIME_TICKET_FARMACIA;
		case "IMPRIME_BA_EMERGENCIA":
			return IMPRIME_BA_EMERGENCIA; 
		case "LAUDO_ACOMP":
			return LAUDO_ACOMP;
		case "LAUDO_CTI":
			return LAUDO_CTI;
		case "NAO_APRAZA_CUIDADO_PEN":
			return NAO_APRAZA_CUIDADO_PEN;
		case "NAO_APRAZA_CUIDADO_PME":
			return NAO_APRAZA_CUIDADO_PME;
		case "NAO_APRAZA_MDTO_PME":
			return NAO_APRAZA_MDTO_PME;
		case "NAO_FATURAR_CONVENIOS":
			return NAO_FATURAR_CONVENIOS;
		case "NAO_OBTEM_DILUICAO":
			return NAO_OBTEM_DILUICAO;
		case "OBTEM_DILUICAO":
			return OBTEM_DILUICAO;
		case "PATOLOGIA_CLINICA":
			return PATOLOGIA_CLINICA;
		case "PEN_CONSECUTIVA":
			return PEN_CONSECUTIVA;
		case "PEN_INFORMATIZADA":
			return PEN_INFORMATIZADA;
		case "PERMITE_AGRUPAR_EXAMES":
			return PERMITE_AGRUPAR_EXAMES;
		case "PERMITE_ATOS_ANESTESICOS":
			return PERMITE_ATOS_ANESTESICOS;
		case "PERMITE_INF_PRESCRIBENTE":
			return PERMITE_INF_PRESCRIBENTE;
		case "PERMITE_NOTIFICAR_MCI_PACIENTE":
			return PERMITE_NOTIFICAR_MCI_PACIENTE;
		case "PERMITE_PACIENTE_EXTRA":
			return PERMITE_PACIENTE_EXTRA;
		case "PERMITE_PRESCRICAO_BI":
			return PERMITE_PRESCRICAO_BI;
		case "PERMITE_SUMARIO_ALTA_MANUAL":
			return PERMITE_SUMARIO_ALTA_MANUAL;
		case "PME_CONSECUTIVA":
			return PME_CONSECUTIVA;
		case "PME_INFORMATIZADA":
			return PME_INFORMATIZADA;
		case "POSSUI_IMPRESSORA_PADRAO":
			return POSSUI_IMPRESSORA_PADRAO;
		case "POSSUI_QRTO_EXCLUSIV_INFEC":
			return POSSUI_QRTO_EXCLUSIV_INFEC;
		case "PROTOCOLA_PACIENTE":
			return PROTOCOLA_PACIENTE;
		case "REC_HUMANOS":	
			return REC_HUMANOS;
		case "REGISTRO_ATEND_AMB_PARA_EXAMES":
			return REGISTRO_ATEND_AMB_PARA_EXAMES;
		case "SALA_GESSO":
			return SALA_GESSO;
		case "SALA_RECUPERACAO":
			return SALA_RECUPERACAO;
		case "SMO":
			return SMO;
		case "SOLICITA_EXAMES_PELO_SISTEMA":
			return SOLICITA_EXAMES_PELO_SISTEMA;
		case "SOLICITA_RESPONSAVEL":
			return SOLICITA_RESPONSAVEL;
		case "TICKET_EXAME_PAC_EXTERNO":
			return TICKET_EXAME_PAC_EXTERNO;
		case "UNID_AMBULATORIO":
			return UNID_AMBULATORIO;
		case "UNID_APLICACAO_QUIMIOTERAPIA":
			return UNID_APLICACAO_QUIMIOTERAPIA;
		case "UNID_BANCO_SANGUE":
			return UNID_BANCO_SANGUE;
		case "UNID_BIOQUIMICA":
			return UNID_BIOQUIMICA;
		case "UNID_COLETA":
			return UNID_COLETA;
		case "UNID_CONVENIO":
			return UNID_CONVENIO;
		case "UNID_CTI":
			return UNID_CTI;
		case "UNID_CTI_POS_OPER_CARD":
			return UNID_CTI_POS_OPER_CARD;
		case "UNID_ECOGRAFIA":
			return UNID_ECOGRAFIA;
		case "UNID_EMERGENCIA":
			return UNID_EMERGENCIA;
		case "UNID_EXECUTORA_CIRURGIAS":
			return UNID_EXECUTORA_CIRURGIAS;
		case "UNID_EXECUTORA_EXAMES":
			return UNID_EXECUTORA_EXAMES;
		case "UNID_FARMACIA":
			return UNID_FARMACIA;
		case "UNID_FISIATRIA":
			return UNID_FISIATRIA;
		case "UNID_GENETICA":
			return UNID_GENETICA;
		case "UNID_HEMATOLOGIA":
			return UNID_HEMATOLOGIA;
		case "UNID_HOSP_DIA":
			return UNID_HOSP_DIA;
		case "UNID_IMUNOLOGIA":
			return UNID_IMUNOLOGIA;
		case "UNID_INTERNACAO":
			return UNID_INTERNACAO;
		case "UNID_MED_NUCLEAR":
			return UNID_MED_NUCLEAR;
		case "UNID_MICROBIOLOGIA":
			return UNID_MICROBIOLOGIA;
		case "UNID_NEONATOLOGIA":
			return UNID_NEONATOLOGIA;
		case "UNID_OBSTETRICA":
			return UNID_OBSTETRICA;
		case "UNID_OFTALMO":
			return UNID_OFTALMO;
		case "UNID_OTORRINO":
			return UNID_OTORRINO;
		case "UNID_PATOLOGIA":
			return UNID_PATOLOGIA;
		case "UNID_PESQUISA":
			return UNID_PESQUISA;
		case "UNID_PNEUMOLOGIA":
			return UNID_PNEUMOLOGIA;
		case "UNID_PSIQUIATRICA":
			return UNID_PSIQUIATRICA;
		case "UNID_RADIODIAGNOSTICO":
			return UNID_RADIODIAGNOSTICO;
		case "UNID_RADIOIMUNOENSAIO":
			return UNID_RADIOIMUNOENSAIO;
		case "UNID_RADIOLOGIA":
			return UNID_RADIOLOGIA;
		case "UNID_SATELITE_IG":
			return UNID_SATELITE_IG;
		case "UNID_SOROLOGIA_DOADORES":
			return UNID_SOROLOGIA_DOADORES;
		case "UNID_URGENCIA":
			return UNID_URGENCIA;
		case "UNID_UTIN":
			return UNID_UTIN;
		case "UNID_UTIP":
			return UNID_UTIP;
		case "UNID_ZONA_14":
			return UNID_ZONA_14;
		case "USA_NOVO_LAUDO":
			return USA_NOVO_LAUDO;
		case "USA_SELO_LAUDO":
			return USA_SELO_LAUDO;
		case "VERF_ESCALA_PROF_INT":
			return VERF_ESCALA_PROF_INT;
		case "ZONA_AMBULATORIO":
			return ZONA_AMBULATORIO;
		case "ALERTA_ATENDER_LEITO_TRANSF":
			return ALERTA_ATENDER_LEITO_TRANSF;
		case "CONTROLES_PACIENTE_INFORMATIZADO":
			return CONTROLES_PACIENTE_INFORMATIZADO;
		case "INFORMA_ESTADO_SAUDE":
			return INFORMA_ESTADO_SAUDE;
		case "UNID_FARMACIA_DISPENSARIO":
			return UNID_FARMACIA_DISPENSARIO;
		case "INTEGRACAO_DISPENSARIO_SEM_AVAL_FARM":
			return INTEGRACAO_DISPENSARIO_SEM_AVAL_FARM;	
		case "UBS":
			return UBS;
		case "IMPRIME_ETIQUETAS_AMOSTRA":
			return IMPRIME_ETIQUETAS_AMOSTRA;
		case "IMPRIME_PRESCRICAO_FARMACIA":
			return IMPRIME_PRESCRICAO_FARMACIA;	
		default:
			return null;
		}
	}
	
}