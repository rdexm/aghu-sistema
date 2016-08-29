package br.gov.mec.aghu.ambulatorio.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterReceituarioRN extends BaseBusiness {





private static final Log LOG = LogFactory.getLog(ManterReceituarioRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MamReceituariosDAO mamReceituariosDAO;
	
	
	public enum ManterReceituarioRNExceptionCode implements BusinessExceptionCode {
		MAM_00574
		;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1736958073788828139L;

	/**
	 * Insere objeto MamReceituarios.
	 * 
	 * @param {MamReceituarios} receita
	 * @throws ApplicationBusinessException
	 */
	public void atualizarReceituario(MamReceituarios receita) throws ApplicationBusinessException {
		this.preAtualizarReceituario(receita);
		
		this.getMamReceituariosDAO().atualizar(receita);
		this.getMamReceituariosDAO().flush();
	}
	
	/**
	 * @ORADB TRIGGER AGH.MAMT_RCT_BRU<br>
	 * 
	 * Metodo NAO implementado, pois era apenas para validacao de permissao.<br>
	 * 
	 * @param receita
	 */
	private void preAtualizarReceituario(MamReceituarios receita) {
		/*
		CREATE OR REPLACE TRIGGER "AGH"."MAMT_RCT_BRU" 
		 BEFORE UPDATE
		 ON MAM_RECEITUARIOS
		 FOR EACH ROW
		BEGIN
		// Este código foi incluido como solução dos casos
		// que doutorandos estavam conseguindo assinar sem ter permissão.
		// 03/02/2006 por Rosane.
		DECLARE
		--
		v_tp_rec_geral     VARCHAR2(1):=NULL;
		v_tp_rec_especial  VARCHAR2(1):=NULL;
		v_assina           VARCHAR2(1):='N';
		--
		v_vlr_data              DATE;
		v_vlr_numero	      NUMBER;
		v_vlr_texto             VARCHAR2(2000);
		v_msg                   VARCHAR2(500);
		--
		  CURSOR cur_proc_rec IS
		    SELECT roc_seq
		      FROM mam_tipo_receit_processos
		     WHERE ter_seq IN (v_tp_rec_geral, v_tp_rec_especial)
		       AND ind_situacao = 'A';
		--
		BEGIN
		IF USER = 'AGH' THEN -- Milena para criação coluna version.
			RETURN;
		END IF;
		// CHAVEAMENTO DE TRIGGER: Se FOR usuário AGHU sai da PROCEDURE - Milena 10/12/10
		IF ( aghc_ver_usuario_aghu(USER) = TRUE ) THEN
		   RETURN;
		END IF;
		// se esta tentando validar verifica se o usuário realmente pode assinar
		IF aghk_util.modificados (:old.ind_pendente, :new.ind_pendente) AND
		   :new.ind_pendente = 'V'
		THEN
		   //-- busca tipo da receita geral
		   aghp_get_parametro ('P_TP_REC_GERAL','MAMT_RCT_BRU',
		                       'N','N',v_vlr_data, v_tp_rec_geral, v_vlr_texto, v_msg);
		   //-- busca tipo da receita especial
		   aghp_get_parametro ('P_TP_REC_ESPECIAL','MAMT_RCT_BRU',
		                       'N','N',v_vlr_data, v_tp_rec_especial, v_vlr_texto, v_msg);
		   //-- verifica acessos
		   FOR r_proc IN cur_proc_rec LOOP
		       v_assina := mamk_perfil.mamc_valida_processo (:new.ser_vin_codigo_valida, :new.ser_matricula_valida, r_proc.roc_seq);
		       IF v_assina = 'S' THEN
		          EXIT;
		       END IF;
		   END LOOP;
		   IF v_assina <> 'S' THEN
		      raise_application_error(-20000,'MAM-03125');
		   END IF;
		END IF;
		END;
		BEGIN
		//verifica se o receituário já foi validado
		IF :old.ind_pendente = 'V'
		THEN
		   IF aghk_util.modificados (:old.observacao, :new.observacao)
		   OR aghk_util.modificados (:old.nro_vias,  :new.nro_vias)
		   OR aghk_util.modificados (:old.tipo,      :new.tipo)
		   THEN
		     mamk_rct_rn.rn_rctp_alterar;
		   END IF;
		END IF;
		END;
		//--incrementa o campo version utilizado pelo AGHU
		:new.version := NVL( :old.version, 0 ) + 1;
		END MAMT_RCT_BRU;
		/
		ALTER TRIGGER "AGH"."MAMT_RCT_BRU" ENABLE;		
		*/
	}

	/**
	 * Remove objeto MamReceituarios.
	 * 
	 * @param {MamReceituarios} receita
	 * @throws ApplicationBusinessException
	 */
	public void removerReceituario(MamReceituarios receita) throws ApplicationBusinessException {

		this.preRemoverReceituario(receita);
		this.getMamReceituariosDAO().remover(receita);
		this.getMamReceituariosDAO().flush();

	}
	
	/**
	 * @ORADB Trigger AGH.MAMT_RCT_BRD
	 * 
	 * @param {MamReceituarios} receita
	 * @throws ApplicationBusinessException
	 */
	private void preRemoverReceituario(MamReceituarios receita) throws ApplicationBusinessException {
		MamReceituarios receituarioOriginal = this.getMamReceituariosDAO().obterMamReceituarioOriginal(receita);
		if (receituarioOriginal != null && DominioIndPendenteAmbulatorio.V == receituarioOriginal.getPendente()) {
			throw new ApplicationBusinessException(ManterReceituarioRNExceptionCode.MAM_00574);
		}
	}

	private MamReceituariosDAO getMamReceituariosDAO() {
		return mamReceituariosDAO;
	}

}
