package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapTipoAfastamento;
import br.gov.mec.aghu.registrocolaborador.dao.RapTipoAfastamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class TipoAfastamentoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(TipoAfastamentoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private RapTipoAfastamentoDAO rapTipoAfastamentoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8778810012413480354L;

	public enum TipoAfastamentoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_TIPO_AFASTAMENTO_NAO_INFORMADO, MENSAGEM_DESCRICAO_TIPO_AFASTAMENTO_NAO_INFORMADA, MENSAGEM_TIPO_AFASTAMENTO_UTILIZADO, MENSAGEM_INDICES_INVALIDOS, MENSAGEM_CODIGOS_STARH_INVALIDOS, MENSAGEM_DESCRICAO_SITUACAO_INVALIDOS;
	}

	public void validaExclusaoTipoAfastamento(RapTipoAfastamento tipoAfastamento)
			throws ApplicationBusinessException {
		if (tipoAfastamento == null || StringUtils.isEmpty(tipoAfastamento.getCodigo())) {
			throw new ApplicationBusinessException(
					TipoAfastamentoRNExceptionCode.MENSAGEM_TIPO_AFASTAMENTO_NAO_INFORMADO);
		}
		
		Long registros = getTipoAfastamentoDAO().pesquisarTipoAfastamentoCount(tipoAfastamento);
		logDebug("TipoAfastamentoRN.validaExclusaoTipoAfastamento(): afastamentos para o tipo ["
				+ tipoAfastamento.getCodigo() + "]: [" + registros + "].");

		if (registros > 0) {
			throw new ApplicationBusinessException(
					TipoAfastamentoRNExceptionCode.MENSAGEM_TIPO_AFASTAMENTO_UTILIZADO);
		}
	}
	
	public void validaPersistenciaTipoAfastamento(RapTipoAfastamento tipoAfastamento)
		throws ApplicationBusinessException {
		if (tipoAfastamento == null || StringUtils.isEmpty(tipoAfastamento.getCodigo())) {
			throw new ApplicationBusinessException(
					TipoAfastamentoRNExceptionCode.MENSAGEM_TIPO_AFASTAMENTO_NAO_INFORMADO);
		}
		if (StringUtils.isEmpty(tipoAfastamento.getDescricao())) {
			throw new ApplicationBusinessException(TipoAfastamentoRNExceptionCode.MENSAGEM_DESCRICAO_TIPO_AFASTAMENTO_NAO_INFORMADA);
		}
		/*
		 * Só aceita as combinações abaixo entre IND_EXIGE_CONSULTA E IND_PERMITE_CID, senão da erro com mensagem RAP-00326
 			(IND_EXIGE_CONSULTA = 'S' AND IND_PERMITE_CID = ' ') OR
 			(IND_EXIGE_CONSULTA = 'N' AND IND_PERMITE_CID = 'N') OR
 			(IND_EXIGE_CONSULTA = 'N' AND IND_PERMITE_CID = 'S') OR
 			(IND_EXIGE_CONSULTA = 'N' AND IND_PERMITE_CID = ' ') 
		 */
		boolean indicesValidos = false;
		DominioSimNao indExigeConsulta = tipoAfastamento.getIndExigeConsulta();
		DominioSimNao indPermiteCid = tipoAfastamento.getIndPermiteCid();
		if (indExigeConsulta != null) {
			if (indExigeConsulta.isSim() && indPermiteCid == null) {
				indicesValidos = true;
			} else if (! indExigeConsulta.isSim()) {
				indicesValidos = true;
			}
		}
		if (! indicesValidos) {
			throw new ApplicationBusinessException(TipoAfastamentoRNExceptionCode.MENSAGEM_INDICES_INVALIDOS);
		}
		/*
		 * Só deve aceitar ambos COD_CAUSA_STARH, COD_MOTIVO_STARH PREENCHIDOS OU  ambos NULOS. Senão RAP-00541
		 */
		boolean codCausaStarhNull = (tipoAfastamento.getCodCausaStarh() == null);
		boolean codMotivoStarhNull = StringUtils.isEmpty(StringUtils.trimToNull(tipoAfastamento.getCodMotivoStarh()));
		//XOR operator
		boolean starhInvalido = codCausaStarhNull ^ codMotivoStarhNull;
		if (starhInvalido) {
			throw new ApplicationBusinessException(TipoAfastamentoRNExceptionCode.MENSAGEM_CODIGOS_STARH_INVALIDOS);
		}
	}
	
	/**
	 * Tipo de afastamento não deve aceitar descrições duplicadas para a mesma situação
	 */
	public void validaConsistenciaDescricaoSituacao(RapTipoAfastamento tipoAfastamento) throws ApplicationBusinessException {
		String descricao = tipoAfastamento.getDescricao();
		DominioSituacao situacao = tipoAfastamento.getIndSituacao();
		boolean inconsistentes = false;
		//monta consulta
		RapTipoAfastamento example = new RapTipoAfastamento();
		example.setDescricao(StringUtils.trimToNull(descricao));
		example.setIndSituacao(situacao);
		//resultados
		List<RapTipoAfastamento> rapTipoAfastamentos = getTipoAfastamentoDAO().pesquisarTipoAfastamento(example);
		if (rapTipoAfastamentos != null && ! rapTipoAfastamentos.isEmpty()) {
			for (RapTipoAfastamento rapTipoAfastamento : rapTipoAfastamentos) {
				if (!rapTipoAfastamento.equals(tipoAfastamento)
						&& StringUtils.equalsIgnoreCase(descricao, rapTipoAfastamento.getDescricao())
						&& rapTipoAfastamento.getIndSituacao().equals(situacao)) {
					inconsistentes = true;
				}
			}
		}
		if (inconsistentes) {
			throw new ApplicationBusinessException(TipoAfastamentoRNExceptionCode.MENSAGEM_DESCRICAO_SITUACAO_INVALIDOS);
		}
	}
	
	protected RapTipoAfastamentoDAO getTipoAfastamentoDAO() {
		return rapTipoAfastamentoDAO;
	}	
}
