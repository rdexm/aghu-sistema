package br.gov.mec.aghu.faturamento.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.model.FatItemContaApac;


/**
 * Triggers para: <br/>
 * ORADB: <code>FAT_ITENS_CONTA_APAC</code>
 * @author gandriotti
 *
 */
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
@Stateless
public class ItemContaApacRN extends AbstractAGHUCrudRn<FatItemContaApac> {
	
	private static final Log LOG = LogFactory.getLog(ItemContaApacRN.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8200547246060733796L;

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * TODO
	 * <p> 
	 * ORADB: <code>FATT_ICA_ARD</code> <br/>
	 * </p>
	 */
//	@Override
//	protected boolean ardPosRemocaoRow(FatItemContaApac entidade)
//			throws BaseException {
//
//		CREATE OR REPLACE TRIGGER "AGH"."FATT_ICA_ARD" AFTER
//		  DELETE
//		    ON FAT_ITENS_CONTA_APAC FOR EACH ROW BEGIN
//		    -- Se usuário for = "BACKUP", que faz a limpeza,  Não executar lógica de "
//		    -- DELETE"
//		    IF aghc_get_user_banco = 'BACKUP' THEN RETURN;
//		END IF;
//		/* QMS$JOURNALLING */
//		BEGIN
//		  INSERT
//		  INTO
//		    fat_itens_conta_apac_jn
//		    (
//		      jn_user ,
//		      jn_date_time ,
//		      jn_operation ,
//		      cap_atm_numero ,
//		      cap_seqp ,
//		      seqp ,
//		      quantidade ,
//		      ind_situacao ,
//		      local_cobranca ,
//		      criado_por ,
//		      criado_em ,
//		      alterado_por ,
//		      alterado_em ,
//		      valor ,
//		      phi_seq ,
//		      ser_matricula ,
//		      ser_vin_codigo ,
//		      ise_seqp ,
//		      ise_soe_seq ,
//		      prh_con_numero ,
//		      prh_phi_seq ,
//		      dthr_realizado ,
//		      ppc_crg_seq ,
//		      ppc_epr_pci_seq ,
//		      ppc_epr_esp_seq ,
//		      ppc_ind_resp_proc ,
//		      pmr_seq
//		    )
//		    VALUES
//		    (
//		      USER ,
//		      sysdate ,
//		      'DEL' ,
//		      :old.cap_atm_numero ,
//		      :old.cap_seqp ,
//		      :old.seqp ,
//		      :old.quantidade ,
//		      :old.ind_situacao ,
//		      :old.local_cobranca ,
//		      :old.criado_por ,
//		      :old.criado_em ,
//		      :old.alterado_por ,
//		      :old.alterado_em ,
//		      :old.valor ,
//		      :old.phi_seq ,
//		      :old.ser_matricula ,
//		      :old.ser_vin_codigo ,
//		      :old.ise_seqp ,
//		      :old.ise_soe_seq ,
//		      :old.prh_con_numero ,
//		      :old.prh_phi_seq ,
//		      :old.dthr_realizado ,
//		      :old.ppc_crg_seq ,
//		      :old.ppc_epr_pci_seq ,
//		      :old.ppc_epr_esp_seq ,
//		      :old.ppc_ind_resp_proc ,
//		      :old.pmr_seq
//		    );
//		END;
//		END FATT_ICA_ARD;
//		/
//		
//		return super.ardPosRemocaoRow(entidade);
//	}
	
	/**
	 * TODO
	 * <p> 
	 * ORADB: <code>FATT_ICA_ARU</code> <br/>
	 * </p>
	 */
//	@Override
//	protected boolean aruPosAtualizacaoRow(
//			FatItemContaApac original,
//			FatItemContaApac modificada)
//			throws BaseException {
//
//		CREATE OR REPLACE TRIGGER "AGH"."FATT_ICA_ARU" AFTER
//		  UPDATE
//		    ON FAT_ITENS_CONTA_APAC FOR EACH ROW DECLARE BEGIN
//		    /* QMS$JOURNALLING */
//		    BEGIN IF :old.cap_atm_numero <> :new.cap_atm_numero
//		  OR :old.cap_seqp               <> :new.cap_seqp
//		  OR :old.seqp                   <> :new.seqp
//		  OR :old.quantidade             <> :new.quantidade
//		  OR :old.ind_situacao           <> :new.ind_situacao
//		  OR :old.local_cobranca         <> :new.local_cobranca
//		  OR :old.criado_por             <> :new.criado_por
//		  OR :old.criado_em              <> :new.criado_em
//		  OR :old.alterado_por           <> :new.alterado_por
//		  OR :old.alterado_em            <> :new.alterado_em
//		  OR :old.valor                  <> :new.valor
//		  OR :old.phi_seq                <> :new.phi_seq
//		  OR :old.ser_matricula          <> :new.ser_matricula
//		  OR :old.ser_vin_codigo         <> :new.ser_vin_codigo
//		  OR :old.ise_seqp               <> :new.ise_seqp
//		  OR :old.ise_soe_seq            <> :new.ise_soe_seq
//		  OR :old.prh_con_numero         <> :new.prh_con_numero
//		  OR :old.prh_phi_seq            <> :new.prh_phi_seq
//		  OR :old.dthr_realizado         <> :new.dthr_realizado
//		  OR :old.ppc_crg_seq            <> :new.ppc_crg_seq
//		  OR :old.ppc_epr_pci_seq        <> :new.ppc_epr_pci_seq
//		  OR :old.ppc_epr_esp_seq        <> :new.ppc_epr_esp_seq
//		  OR :old.ppc_ind_resp_proc      <> :new.ppc_ind_resp_proc
//		  OR :old.pmr_seq                <> :new.pmr_seq THEN
//		  INSERT
//		  INTO
//		    fat_itens_conta_apac_jn
//		    (
//		      jn_user ,
//		      jn_date_time ,
//		      jn_operation ,
//		      cap_atm_numero ,
//		      cap_seqp ,
//		      seqp ,
//		      quantidade ,
//		      ind_situacao ,
//		      local_cobranca ,
//		      criado_por ,
//		      criado_em ,
//		      alterado_por ,
//		      alterado_em ,
//		      valor ,
//		      phi_seq ,
//		      ser_matricula ,
//		      ser_vin_codigo ,
//		      ise_seqp ,
//		      ise_soe_seq ,
//		      prh_con_numero ,
//		      prh_phi_seq ,
//		      dthr_realizado ,
//		      ppc_crg_seq ,
//		      ppc_epr_pci_seq ,
//		      ppc_epr_esp_seq ,
//		      ppc_ind_resp_proc ,
//		      pmr_seq
//		    )
//		    VALUES
//		    (
//		      USER ,
//		      sysdate ,
//		      'UPD' ,
//		      :old.cap_atm_numero ,
//		      :old.cap_seqp ,
//		      :old.seqp ,
//		      :old.quantidade ,
//		      :old.ind_situacao ,
//		      :old.local_cobranca ,
//		      :old.criado_por ,
//		      :old.criado_em ,
//		      :old.alterado_por ,
//		      :old.alterado_em ,
//		      :old.valor ,
//		      :old.phi_seq ,
//		      :old.ser_matricula ,
//		      :old.ser_vin_codigo ,
//		      :old.ise_seqp ,
//		      :old.ise_soe_seq ,
//		      :old.prh_con_numero ,
//		      :old.prh_phi_seq ,
//		      :old.dthr_realizado ,
//		      :old.ppc_crg_seq ,
//		      :old.ppc_epr_pci_seq ,
//		      :old.ppc_epr_esp_seq ,
//		      :old.ppc_ind_resp_proc ,
//		      :old.pmr_seq
//		    );
//		END IF;
//		END;
//		END;
//		/		
//
//		return super.aruPosAtualizacaoRow(
//				original, modificada);
//	}
	
	/**
	 * TODO
	 * <p> 
	 * ORADB: <code>FATT_ICA_BASE_BRI</code> <br/>
	 * ORADB: <code>FATT_ICA_BRI</code> <br/>
	 * </p>
	 */
//	@Override
//	protected boolean briPreInsercaoRow(FatItemContaApac entidade)
//			throws BaseException {
//	
//		CREATE OR REPLACE TRIGGER "AGH"."FATT_ICA_BASE_BRI" BEFORE
//		  INSERT
//		    ON FAT_ITENS_CONTA_APAC FOR EACH ROW BEGIN
//		    -- VER INTEGRIDADE COM AEL_ITEM_SOLICITACAO_EXAMES
//		    fatp_ver_item_solic
//		    (
//		      :new.ISE_SOE_SEQ,
//		      :new.ISE_SEQP
//		    );
//		END FATT_ICA_BASE_BRI;
//		/
		
//		CREATE OR REPLACE TRIGGER "AGH"."FATT_ICA_BRI" BEFORE
//		  INSERT
//		    ON FAT_ITENS_CONTA_APAC FOR EACH ROW DECLARE BEGIN
//		    -- VERIFICA SE O CONVENIO E PLANO SAO VALIDOS PARA ESSE PROCEDIMENTO
//		    fatp_ver_filtro_conv_plano
//		    (
//		      NULL,
//		      :NEW.CAP_ATM_NUMERO,
//		      :NEW.PMR_SEQ,
//		      NULL,
//		      NULL,
//		      :NEW.PHI_SEQ,
//		      NULL,
//		      NULL,
//		      NULL
//		    );
//		  fatk_sus_rn.rn_fatp_atu_servidor
//		  (
//		    :new.ser_matricula   -- p_new_matricula
//		    ,:new.ser_vin_codigo -- p_new_vinculo
//		  )
//		  ;
//		  /* QMS$DATA_AUDITING */
//		  DECLARE
//		    CURSOR c_dual
//		    IS
//		      SELECT
//		        sysdate ,
//		        USER
//		      FROM
//		        dual;
//		    l_sysdate DATE;
//		    l_user VARCHAR2(30);
//		  BEGIN
//		    OPEN c_dual;
//		    FETCH
//		      c_dual
//		    INTO
//		      l_sysdate ,
//		      l_user;
//		    CLOSE c_dual;
//		    :new.CRIADO_EM    := l_sysdate;
//		    :new.CRIADO_POR   := l_user;
//		    :new.ALTERADO_EM  := l_sysdate;
//		    :new.ALTERADO_POR := l_user;
//		  END;
//		END;
//		/
//		
//		
//		return super.briPreInsercaoRow(entidade);
//	}
	
	/**
	 * TODO
	 * <p> 
	 * ORADB: <code>FATT_ICA_BASE_BRU</code> <br/>
	 * ORADB: <code>FATT_ICA_BRU</code> <br/>
	 * </p>
	 */
//	@Override
//	protected boolean bruPreAtualizacaoRow(
//			FatItemContaApac original,
//			FatItemContaApac modificada)
//			throws BaseException {
//
//		CREATE OR REPLACE TRIGGER "AGH"."FATT_ICA_BASE_BRU" BEFORE
//		  UPDATE
//		    ON FAT_ITENS_CONTA_APAC FOR EACH ROW BEGIN
//		    -- VER INTEGRIDADE COM AEL_ITEM_SOLICITACAO_EXAMES
//		    IF NVL(:old.ISE_SOE_SEQ,0) <> :new.ISE_SOE_SEQ
//		  OR NVL(:old.ISE_SEQP,0)      <> :new.ISE_SEQP THEN fatp_ver_item_solic(
//		    :new.ISE_SOE_SEQ, :new.ISE_SEQP);
//		END IF;
//		END FATT_ICA_BASE_BRU;
//		/
		
//		CREATE OR REPLACE TRIGGER "AGH"."FATT_ICA_BRU" BEFORE
//		  UPDATE
//		    ON FAT_ITENS_CONTA_APAC FOR EACH ROW DECLARE BEGIN
//		    -- VERIFICA SE O CONVENIO E PLANO SAO VALIDOS PARA ESSE PROCEDIMENTO
//		    IF :NEW.CAP_ATM_NUMERO != :old.CAP_ATM_NUMERO
//		  OR :NEW.PMR_SEQ          != :old.PMR_SEQ
//		  OR :NEW.PHI_SEQ          != :old.PHI_SEQ
//		  OR :new.ind_situacao     != 'C' THEN fatp_ver_filtro_conv_plano ( NULL,
//		    :NEW.CAP_ATM_NUMERO, :NEW.PMR_SEQ, NULL, NULL, :NEW.PHI_SEQ, NULL, NULL,
//		    NULL );
//		END IF;
//		/* QMS$DATA_AUDITING */
//		DECLARE
//		  CURSOR c_dual
//		  IS
//		    SELECT
//		      sysdate ,
//		      USER
//		    FROM
//		      dual;
//		  l_sysdate DATE;
//		  l_user VARCHAR2(30);
//		BEGIN
//		  OPEN c_dual;
//		  FETCH
//		    c_dual
//		  INTO
//		    l_sysdate ,
//		    l_user;
//		  CLOSE c_dual;
//		  :new.ALTERADO_EM  := l_sysdate;
//		  :new.ALTERADO_POR := l_user;
//		END;
//		END;
//		/
//		
//		return super.bruPreAtualizacaoRow(
//				original, modificada);
//	}
	
	/**
	 * TODO
	 * <p> 
	 * ORADB: <code>FATT_ICA_BRD</code>
	 * </p>
	 */
//	@Override
//	protected boolean brdPreRemocaoRow(FatItemContaApac entidade)
//			throws BaseException {
//	
//
//		CREATE OR REPLACE TRIGGER "AGH"."FATT_ICA_BRD" BEFORE
//		  DELETE
//		    ON FAT_ITENS_CONTA_APAC FOR EACH ROW BEGIN
//		    -- Se usuário for = "BACKUP", que faz a limpeza,  Não executar lógica de "
//		    -- DELETE"
//		    IF aghc_get_user_banco = 'BACKUP' THEN RETURN;
//		END IF;
//		/*if :old.ind_situacao in ('E', 'P') then
//		raise_application_error (-20001,
//		'Item de APAC já está faturado - FATT_ICA_BRD');
//		end if;
//		*/
//		-- atualiza itens de apac quando o ambulatorio eh cancelado
//		/*  if :old.ind_situacao = 'A' and :old.pmr_seq is not null then
//		update fat_proced_amb_realizados
//		set ind_situacao = 'A',
//		local_cobranca = 'B'
//		where seq = :old.pmr_seq
//		and ind_situacao = 'T';
//		end if;      */
//		END FATT_ICA_BRD;
//		/
//		
//		return super.brdPreRemocaoRow(entidade);
//	}
}
