package br.gov.mec.aghu.farmacia.business.exception;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("ucd")
public enum FarmaciaExceptionCode implements BusinessExceptionCode {

	/**
	 * Tipo de Apresentação de Medicamentos com esta Sigla já cadastrado
	 */
	AFA_00027,
	
	/**
	 * Tipo de Uso de Medicamentos com esta Sigla já cadastrado
	 */
	AFA_00036,

	/**
	 * Forma Dosagem com este Material, Seq já cadastrado.
	 * Alterado para:  Forma Dosagem com este medicamento já cadastrado.
	 */
	AFA_00044,
	
	/**
	 * Via de administração já cadastrada para este medicamento.
	 */
	AFA_00061,
		
	/**
	 * Não existe Servidor com este Vínculo e Matrícula.
	 */
	AFA_00169,

	/**
	 * O grupo uso medicamentos deve estar ativo
	 */
	AFA_00170,

	/**
	 * A descrição do Tipo de Uso de Medicamentos não pode ser alterada
	 */
	AFA_00171,

	/**
	 * Não é possível excluir o registro por estar fora do período permitido
	 * para exclusão
	 */
	AFA_00172,

	/**
	 * Erro na recuperação do parâmetro P_DIAS_PERM_DEL na
	 * AFAK_TUM_RN.RN_TUMP_VER_DELECAO
	 */
	AFA_00173,

	/**
	 * Descrição e o Responsável pela avaliação do grupo de uso do medicamento
	 * não podem ser alterados.
	 */
	AFA_00175,

	/**
	 * A via de administração deve estar ativa para ser associada a um
	 * medicamento.
	 */
	AFA_00185,

	/**
	 * A associação medicamento e via de administração não pode ser excluída,
	 * inative.
	 */
	AFA_00186,

	/**
	 * O Tipo Apresentação do Medicamento não pode ser inativada se algum
	 * medicamento ativo o usa
	 */
	AFA_00187,

	/**
	 * A descrição do Tipo Apresentação do Medicamento não pode ser alterado
	 */
	AFA_00188,

	/**
	 * O medicamento deve estar ativo para informar sinônimos
	 */
	AFA_00191,

	/**
	 * Não existe medicamento com este código cadastrado
	 */
	AFA_00204,

	/**
	 * A unidade de medida médica deve estar ativa
	 */
	AFA_00207,

	/**
	 * Somente podem ser alterados situação, indicador de uso nutrição e
	 * indicador de uso prescrição
	 */
	AFA_00208,
	
	/**
	 * O valor do fator de conversão deve ser maior que 0
	 */
	AFA_FDS_CK4,

	/**
	 * Somente é permitida uma forma de dosagem para uso npt por medicamento
	 */
	AFA_00210,

	/**
	 * Somente é permitida uma forma de dosagem para uso prescrição por
	 * medicamento
	 */
	AFA_00211,
	
	/**
	 * Grupo Componente Npt não cadastrado
	 */
	AFA_00213,
	
	/**
	 * Tipo Composição não cadastrado
	 */
	AFA_00215,
	
	/**
	 * Tipo Velocidade Administração não cadastrado
	 */
	AFA_00220,

	/**
	 * Não é possível alterar o registro por estar fora do período permitido
	 * para alterações
	 */
	AFA_00222,
	
	/**
	 * Componente Npt não cadastrado
	 */
	AFA_00224,
	
	/**
	 * Não é permitido excluir um medicamento.
	 */
	AFA_00226,
	
	/**
	 * Ao ativar o medicamento deverá haver uma forma de dosagem ativa
	 */
	AFA_00227,
	
	/**
	 * É obrigatório informar o local de dispensação do medicamento para cada uma das unidades de internação
	 */
	AFA_00229,
	
	/**
	 * Material deve estar ativo
	 */
	AFA_00230,
	
	/**
	 * Forma de Dosagem não cadastrada
	 */
	AFA_00231,
	
	/**
	 * Forma de Dosagem não está ativa
	 */
	AFA_00232,
	
	/**
	 * Esta Unidade Medida Médica não está ativa
	 */
	AFA_00233,
	
	/**
	 * Esta Unidade Medida Médica não pode ser usado para concentração
	 */
	AFA_00234,
	
	/**
	 * O item da solicitação de revisão da padronização de medicamentos deve ter sido aprovado
	 */
	AFA_00235,
	
	/**
	 * A apresentação do medicamento deve estar ativa.
	 */
	AFA_00237,
	
	/**
	 * A apresentação do medicamento deve ser informado quando o medicamento for ativo
	 */
	AFA_00239,
		
	/**
	 * O uso do medicamento deve ser informado quando o medicamento for ativo
	 */
	AFA_00240,
	
	/**
	 * A forma de dosagem informada deve ser compatível com a do componente informado
	 */
	AFA_00241,
	
	/**
	 * A frequencia do aprazamento deve estar ativa
	 */
	AFA_00246,
	
	/**
	 * Para esta frequencia de aprazamento não é permitido informar a frequencia usual
	 */
	AFA_00248,
	
	/**
	 * Para esta frequencia de aprazamento deve ser informada a frequencia usual
	 */
	AFA_00249,
	
	/**
	 * O tipo de uso do medicamento deve estar ativo
	 */
	AFA_00250,
		
	/**
	 * Ao tornar um medicamento ativo ele deverá ter ao menos uma via de administração relacionada
	 */
	AFA_00251,
	
	/**
	 * Erro update AFA_COMPONENTE_NPTS na RN_MEDP_ATU_COMP_NPT. Contate GSIS
	 */
	AFA_00252,
	
	/**
	 * Erro update AFA_DILUENTES na RN_MEDP_ATU_DILUENTE. Contate GSIS
	 */
	AFA_00265,
	
	/**
	 * Erro insert AFA_FORMA_DOSAGENS na RN_MEDP_ATU_DOSAGEM. Contate GSIS.
	 */
	AFA_00266,
	
	/**
	 * Erro update AFA_VIA_ADM_MDTOS na RN_MEDP_ATU_VIA_ADM. Contate GSIS
	 */
	AFA_00274,
	
	/**
	 * Erro update AFA_FOMRA_DOSAGENS na RN_MEDP_ATU_DOS_INAT. Contate GSIS
	 */
	AFA_00275,
	
	/**
	 * Não existe Unidade Funcional com este Código.
	 */
	AFA_00276,
	
	/**
	 * Erro na recuperação do parâmetro P_UNF_FARM_DISP na AFAK_MED_RN.RN_MEDP_ATU_LCAL_DIS. Contate o GSIS
	 */
	AFA_00277,
	
	/**
	 * Erro na inserção do Local dispensação. Contate GSIS
	 */
	AFA_00278,

	/**
	 * Unidade de medida médica não cadastrada
	 */
	AFA_00300,
	
	/**
	 * Salve as alteracoes da tela, antes de clicar neste botão
	 */
	AFA_00443,
	
	/**
	 * Erro na recuperação do parâmetro P_UNF_FARM_FAPE na AFAK_MED_RN.RN_MEDP_ATU_LCAL_DIS. Contate o GSIS
	 */
	AFA_01179,
	
	/**
	 * A prescrição do prontuário #1 tem dose fracionada.
	 */
	AFA_01221,
	
	/**
	 * As prescrições dos prontuários #1 tem dose fracionada.
	 */
	AFA_01222,
	
	/**
	 * Só pode ser padrão BI se permitir BI
	 */
	AFA_01444,

	/**
	 * Só pode marcar ind_permite_bi = 'S' se o indicador na via de administração for 'S' (Sim)
	 */
	AFA_01446,
	
	/**
	 * Ao escolher fotosensivel <Tempo> deve informar o tempo e a unidade de tempo.
	 */
	AFA_01468,
	
	/**
	 * Ao escolher fotosensivel diferente de <Tempo> NÃO informar o tempo e a unidade de tempo.
	 */
	AFA_01469,
	
	/**
	 * Para medicamentos Quimioterápico, o TIPO_QUIMIO deve ser informado.
	 */
	AFA_01502,
	
	/**
	 * A diferença de dias entre as datas é maior que a permitida
	 */
	MENSAGEM_LIMITE_DIAS_RELATORIO,
	
	/**
	 * O Tipo de apresentação não pode ser removido pois possui medicamento associado
	 */
	MEDICAMENTO_ASSOCIADO_TIPO_APRESENTACAO,
	
	/**
	 * Registro já foi excluído por outro usuário
	 */
	REGISTRO_NULO_EXCLUSAO,
	
	OFG_00005, 
	
	
	ERRO_GERACAO_ARQUIVO, 
	
	MENSAGEM_ERRO_DIRETORIO_INEXISTENTE,
	
	AVALIACAO_EXIGE_JUSTIFICATIVA
	
	;

	public void throwException(Object... params) throws ApplicationBusinessException {
		throw new ApplicationBusinessException(this, params);
	}

	public void throwException(Throwable cause, Object... params)
			throws ApplicationBusinessException {
		// Tratamento adicional para não esconder a excecao de negocio
		// original
		if (cause instanceof ApplicationBusinessException) {
			throw (ApplicationBusinessException) cause;
		}
		throw new ApplicationBusinessException(this, cause, params);
	}
}