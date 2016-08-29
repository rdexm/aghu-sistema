package br.gov.mec.aghu.faturamento.dao;


public class ValoresAIHPorDCIHVOQueryBuilder {
	
	public String builder() {
		
		final StringBuilder sql = new StringBuilder(1800);
		
		sql.append(" select cth.valor_hemat    as hem , dci.codigo_dcih    as dcih , dci.tipo 		     as tipo , eai.cth_seq 	     as cthseq , cth.nro_aih        as nroaih , eai.seqp           as eaiseqp , cth.valor_opm      as protese , tcs.descricao      as descricao , eai.pac_prontuario as prontuario , cth.dt_alta_administrativa as alta , cth.diarias_acompanhante   as acomp , cth.dt_int_administrativa  as dtintadm , cth.dt_alta_administrativa as dtaltaadm , eai.iph_cod_sus_realiz 	 as procedimento , cth.cth_seq_reapresentada  as reapresentada , (cth.dias_uti_mes_inicial+cth.dias_uti_mes_anterior+cth.dias_uti_mes_alta) uti , cth.valor_sh + valor_sh_uti + valor_sh_utie + valor_sh_acomp + valor_sh_rn + valor_sh_hemat + valor_sh_transp as servhosp , cth.valor_sp + valor_sp_uti + valor_sp_utie + valor_sp_acomp + valor_sp_rn + valor_sp_hemat + valor_sp_transp as servprof , cth.valor_sadt + valor_sadt_uti + valor_sadt_utie + valor_sadt_acomp + valor_sadt_rn + valor_sadt_hemat + valor_sadt_transp as sadt ,(select cah.iph_cod_sus iph_cod_sus from agh.fat_campos_medico_audit_aih cah where cah.eai_cth_seq = eai.cth_seq and cah.eai_seq = eai.seqp ) as iphcodsus from  agh.fat_tipos_classif_sec_saude  tcs , agh.fat_documento_cobranca_aihs  dci , agh.fat_valores_conta_hospitalar vct , agh.fat_espelhos_aih             eai , agh.fat_contas_hospitalares      cth  where     cth.dci_codigo_dcih   = dci.codigo_dcih and cth.seq               = eai.cth_seq and cth.seq               = vct.cth_seq and eai.seqp 			  = 1 and dci.cpe_modulo        = :prm_modulo  and dci.cpe_mes           = :prm_mes and dci.cpe_ano           = :prm_ano and tcs.seq 			  = dci.tcs_seq  order by dci.codigo_dcih,  cth.nro_aih ");
		
		
		return sql.toString();
	}
}
