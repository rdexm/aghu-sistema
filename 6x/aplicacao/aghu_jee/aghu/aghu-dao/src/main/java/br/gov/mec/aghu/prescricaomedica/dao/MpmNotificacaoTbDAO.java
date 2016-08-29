package br.gov.mec.aghu.prescricaomedica.dao;


import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseAgravoOutrasDoencas;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseAntirretroviral;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseBaciloscopiaEscarro;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseBeneficiario;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseCulturaEscarro;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseDrogasIlicitas;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseEscolaridade;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseEspecIdade;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseForma;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseHistopatologia;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseHiv;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseImigrantes;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndAids;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndAlcoolismo;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndCutanea;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndDiabetes;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndDoencaMental;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndGangPerif;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndGenitoUrinaria;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndGestante;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndLaringea;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndMeningite;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndMiliar;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndOcular;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndOssea;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndOutraExtraPulmonar;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndPleural;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseLiberdade;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseProfSaude;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseRaca;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseRaioXTorax;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseSensibilidade;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseSexo;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseSitRua;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseTMR;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseTabagismo;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseTipoEntrada;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseTipoNotificacao;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseZona;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.MpmNotificacaoTb;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.GerarPDFSinanVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoNotificacaoTbVO;

public class MpmNotificacaoTbDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmNotificacaoTb> {

	private static final long serialVersionUID = -5331705175843622616L;

	private static final String NTB = "NTB";
	private static final String NTB_DOT = "NTB.";
	private static final String ATD = "ATD";
	private static final String SER = "SER";
	private static final String SER_DOT = "SER.";
	private static final String PES = "PES";
	private static final String PES_DOT = "PES.";
	private static final String CARGO = "CARGO";
	private static final String CARGO_DOT = "CARGO.";
	private static final String ATD_DOT = "ATD.";
	private static final String CID = "CID";
	private static final String CID_DOT = "CID.";
	private static final String UFS = "UFS";
	private static final String UFS_DOT = "UFS.";
	private static final String CDD = "CDD";
	private static final String CDD_DOT = "CDD.";

	public List<MpmNotificacaoTb> pesquisarMpmNotificacaoTbPorAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmNotificacaoTb.class, "NTB");
		criteria.createAlias("NTB." + MpmNotificacaoTb.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		return executeCriteria(criteria);
	}

	public List<PrescricaoNotificacaoTbVO> listarNotificacoesTbPorAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmNotificacaoTb.class);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MpmNotificacaoTb.Fields.SEQ.toString()), PrescricaoNotificacaoTbVO.Fields.SEQ.toString())
				.add(Projections.property(MpmNotificacaoTb.Fields.IND_CONCLUIDO.toString()), PrescricaoNotificacaoTbVO.Fields.IND_CONCLUIDO.toString()));

		criteria.add(Restrictions.eq(MpmNotificacaoTb.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.setResultTransformer(Transformers.aliasToBean(PrescricaoNotificacaoTbVO.class));
		return executeCriteria(criteria);
	}
	
	public List<MpmNotificacaoTb> obterJustificativaParaConfirmacao(MpmPrescricaoMedica prescricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmNotificacaoTb.class, "NTB");
		criteria.createAlias("NTB." + MpmNotificacaoTb.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), prescricao.getId().getAtdSeq()));
		criteria.add(Restrictions.isNull("NTB." + MpmNotificacaoTb.Fields.SERVIDOR_VALIDADO.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Obtém uma Notificação de Tuberculose para impressão do PDF Sinan a partir do código do Atendimento.
	 * @ORADB mpmp_imp_notif_tb
	 * 
	 * @param atdSeq - Código do Atendimento
	 * @return Notificação de Tuberculose
	 */
	public MpmNotificacaoTb obterNotificacaoImpressaoPorAtendimento(Integer atdSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmNotificacaoTb.class, NTB);

		criteria.createAlias(NTB_DOT + MpmNotificacaoTb.Fields.ATENDIMENTO.toString(), ATD);

		criteria.add(Restrictions.eq(ATD_DOT + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(NTB_DOT + MpmNotificacaoTb.Fields.IND_CONCLUIDO.toString(), DominioSimNao.S.isSim()));

		List<MpmNotificacaoTb> retorno = executeCriteria(criteria);

		if (retorno.isEmpty()) {
			return null;
}

		return retorno.get(0);
	}

	/**
	 * Obtém informações para impressão do PDF Sinan a partir do código da Notificação de Tuberculose.
	 * 
	 * @param ntbSeq - Código da Notificação de Tuberculose
	 * @return Informações do PDF Sinan
	 */
	public GerarPDFSinanVO obterInformacoesPdfSinan(Integer ntbSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmNotificacaoTb.class, NTB);

		ProjectionList projections = Projections.projectionList();

		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.SEQ.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.TIPO_NOTIFICACAO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.DOENCA.toString()));
		projections.add(Projections.property(CID_DOT + AghCid.Fields.CODIGO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.DT_NOTIFICACAO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.MUNICIPIO_NOTIFICACAO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.UNIDADE_DE_SAUDE.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.CNES.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.DT_DIAGNOSTICO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.NOME_PACIENTE.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.DT_NASCIMENTO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IDADE.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.ESPECIDADE.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.SEXO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_GESTANTE.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.RACA.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.ESCOLARIDADE.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.NRO_CARTAO_SUS.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.NOME_MAE.toString()));
		projections.add(Projections.property(UFS_DOT + AipUfs.Fields.SIGLA.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.MUNICIPIO_RESIDENCIA.toString()));
		projections.add(Projections.property(CDD_DOT + AipCidades.Fields.COD_IBGE.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.DISTRITO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.BAIRRO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.LOGRADOURO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.CODIGO_LOGRADOURO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.NUMERO_LOGRADOURO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.COMPL_LOGRADOURO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.GEO_CAMPO_1.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.GEO_CAMPO_2.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.PONTO_REFERENCIA.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.CEP.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.DDD_TELEFONE.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.NUMEROTELEFONE.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.ZONA.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.PAIS.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.PRONTUARIO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.TIPO_ENTRADA.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_LIBERDADE.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_PROF_SAUDE.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_SIT_RUA.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_IMIGRANTES.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_BENEFICIARIO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.FORMA.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_PLEURAL.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_GANG_PERIF.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_GENITO_URINARIA.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_OSSEA.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_OCULAR.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_MILIAR.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_MENINGITE.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_CUTANEA.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_LARINGEA.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_OUTRA_EXTRAPULMONAR.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.DESCR_OUTRA_EXTRAPULMONAR.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_AIDS.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_DIABETES.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_DOENCA_MENTAL.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_ALCOOLISMO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_TABAGISMO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_DROGAS_ILICITAS.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_OUTRAS_DOENCAS.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.DESC_OUTRO_AGRAVO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.BACILOSCOPIA_ESCARRO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.RAIOX_TORAX.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.HIV.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_ANTIRETROVIRAL.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.HISTOPATOLOGIA.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.CULTURA_ESCARRO.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_TMR.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.IND_SENSIBILIDADE.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.DT_INICIO_TRAT_ATUAL.toString()));
		projections.add(Projections.property(NTB_DOT + MpmNotificacaoTb.Fields.CONTATOS_REGISTRADOS.toString()));
		
		projections.add(Projections.property(PES_DOT + RapPessoasFisicas.Fields.NOME.toString() ));
		projections.add(Projections.property(CARGO_DOT + RapOcupacaoCargo.Fields.DESCRICAO.toString() ));

		criteria.setProjection(projections);

		criteria.createAlias(NTB_DOT + MpmNotificacaoTb.Fields.CID.toString(), CID, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(NTB_DOT + MpmNotificacaoTb.Fields.UF.toString(), UFS,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(NTB_DOT + MpmNotificacaoTb.Fields.CIDADE.toString(), CDD,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(NTB_DOT + MpmNotificacaoTb.Fields.SERVIDOR_VALIDADO.toString(), SER);
		criteria.createAlias(SER_DOT + RapServidores.Fields.PESSOA_FISICA.toString(), PES, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SER_DOT + RapServidores.Fields.OCUPACAO_CARGO.toString(), CARGO, JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(NTB_DOT + MpmNotificacaoTb.Fields.SEQ.toString(), ntbSeq));

		List<Object[]> retorno = executeCriteria(criteria);

		if (retorno.isEmpty()) {
			return null;
		}

		return preencherObjetoRetornoObterInformacoesPdfSinan(retorno.get(0));
	}

	private GerarPDFSinanVO preencherObjetoRetornoObterInformacoesPdfSinan(Object[] row) {

		GerarPDFSinanVO dados = new GerarPDFSinanVO();

		dados.setSeqNotificacao((Integer) row[0]);
		dados.setDoenca((String) row[2]);
		dados.setCid((String) row[3]);
		dados.setDtNotificacao((Date) row[4]);
		dados.setMunicipioNotificacao((String) row[5]);
		dados.setUnidadeDeSaude((String) row[6]);
		dados.setCnes((Integer) row[7]);
		dados.setDtDiagnostico((Date) row[8]);
		dados.setNomePaciente((String) row[9]);
		dados.setDtNascimento((Date) row[10]);
		dados.setIdade((Short) row[11]);
		dados.setNroCartaoSus((Long) row[17]);
		dados.setNomeMae((String) row[18]);
		dados.setUfSigla((String) row[19]);
		dados.setMunicipioResidencia((String) row[20]);
		dados.setCddCodigo((Integer) row[21]);
		dados.setDistrito((String) row[22]);
		dados.setBairro((String) row[23]);
		dados.setLogradouro((String) row[24]);
		dados.setCodigoLogradouro((Integer) row[25]);
		dados.setNumeroLogradouro((String) row[26]);
		dados.setComplLogradouro((String) row[27]);
		dados.setGeoCampo1((String) row[28]);
		dados.setGeoCampo2((String) row[29]);
		dados.setPontoReferencia((String) row[30]);
		dados.setCep((Integer) row[31]);
		dados.setDddTelefone((Short) row[32]);
		dados.setNumeroTelefone((Long) row[33]);
		dados.setPais((String) row[35]);
		dados.setProntuario((Integer) row[36]);
		dados.setDescExtrapulmonar(montarCampoDescExtrapulmonarObterInformacoesPdfSinan(row));
		dados.setDescPopulacEspecias(montarCampoDescPopulEspecObterInformacoesPdfSinan(row));
		dados.setDescDoeancasAgravosAssociados(montarCampoDescDoeancasAgravosAssociadosPdfSinan(row));
		dados.setDescrOutraExtrapulmonar((String) row[54]);
		dados.setDescOutroAgravo((String) row[62]);
		dados.setDtInicioTratAtual((Date) row[71]);
		dados.setContatosRegistrados((Short) row[72]);
		dados.setNomeMedico((String) row[73]);
		dados.setFuncao((String) row[74]);

		montarCamposEnumObterInformacoesPdfSinan(row, dados);

		return dados;
	}

	private void montarCamposEnumObterInformacoesPdfSinan(Object[] row, GerarPDFSinanVO dados) {

		DominioNotificacaoTuberculoseTipoNotificacao tipoNotificacao = (DominioNotificacaoTuberculoseTipoNotificacao) row[1];
		if (tipoNotificacao != null) {
			dados.setTipoNotificacao(tipoNotificacao.getCodigo() + " - " + tipoNotificacao.getDescricao());
		}

		DominioNotificacaoTuberculoseEspecIdade especIdade = (DominioNotificacaoTuberculoseEspecIdade) row[12];
		if (especIdade != null) {
			dados.setEspecIdade(especIdade.getCodigo() + " - " + especIdade.getDescricao());
		}
		
		DominioNotificacaoTuberculoseSexo sexo = (DominioNotificacaoTuberculoseSexo) row[13];
		if (sexo != null) {
			dados.setSexo(sexo.toString() + " - " + sexo.getDescricao());
		}
		
		DominioNotificacaoTuberculoseIndGestante gestante = (DominioNotificacaoTuberculoseIndGestante) row[14];
		if (gestante != null) {
			dados.setIndGestante(gestante.getCodigo() + " - " + gestante.getDescricao());
		}

		DominioNotificacaoTuberculoseRaca raca = (DominioNotificacaoTuberculoseRaca) row[15];
		if (raca != null) {
			dados.setRaca(raca.getCodigo() + " - " + raca.getDescricao());
		}

		montarCamposEnumObterInformacoesPdfSinanParte2(row, dados);
		montarCamposEnumObterInformacoesPdfSinanParte3(row, dados);
		montarCamposEnumObterInformacoesPdfSinanParte4(row, dados);
		montarCamposEnumObterInformacoesPdfSinanParte5(row, dados);
	}

	private void montarCamposEnumObterInformacoesPdfSinanParte2(Object[] row, GerarPDFSinanVO dados) {

		DominioNotificacaoTuberculoseEscolaridade escolaridade = (DominioNotificacaoTuberculoseEscolaridade) row[16];
		if (escolaridade != null) {
			dados.setEscolaridade(escolaridade.getCodigo() + " - " + escolaridade.getDescricao());
		}

		DominioNotificacaoTuberculoseZona zona = (DominioNotificacaoTuberculoseZona) row[34];
		if (zona != null) {
			dados.setZona(zona.getCodigo() + " - " + zona.getDescricao());
		}

		DominioNotificacaoTuberculoseTipoEntrada tipoEntrada = (DominioNotificacaoTuberculoseTipoEntrada) row[37];
		if (tipoEntrada != null) {
			dados.setTipoEntrada(tipoEntrada.getCodigo() + " - " + tipoEntrada.getDescricao());
		}
		
		DominioNotificacaoTuberculoseLiberdade indLiberdade = (DominioNotificacaoTuberculoseLiberdade) row[38];
		if (indLiberdade != null) {
			dados.setIndLiberdade(indLiberdade.getCodigo());
		}
		
		DominioNotificacaoTuberculoseProfSaude indProfSaude = (DominioNotificacaoTuberculoseProfSaude) row[39];
		if (indProfSaude != null) {
			dados.setIndProfSaude(indProfSaude.getCodigo());
		}
		
		DominioNotificacaoTuberculoseSitRua indSitRua = (DominioNotificacaoTuberculoseSitRua) row[40];
		if (indSitRua != null) {
			dados.setIndSitRua(indSitRua.getCodigo());
		}
	}
	
	private void montarCamposEnumObterInformacoesPdfSinanParte3(Object[] row, GerarPDFSinanVO dados) {

		DominioNotificacaoTuberculoseImigrantes indImigrantes = (DominioNotificacaoTuberculoseImigrantes) row[41];
		if (indImigrantes != null) {
			dados.setIndImigrantes(indImigrantes.getCodigo());
		}
		
		DominioNotificacaoTuberculoseBeneficiario indBeneficiario = (DominioNotificacaoTuberculoseBeneficiario) row[42];
		if (indBeneficiario != null) {
			dados.setIndBeneficiario(indBeneficiario.getCodigo() + " - " + indBeneficiario.getDescricao());
		}
		
		DominioNotificacaoTuberculoseForma forma = (DominioNotificacaoTuberculoseForma) row[43];
		if (forma != null) {
			dados.setForma(forma.getCodigo() + " - " + forma.getDescricao());
		}

		DominioNotificacaoTuberculoseIndAids indAids = (DominioNotificacaoTuberculoseIndAids) row[55];
		if (indAids != null) {
			dados.setIndAids(indAids.getCodigo());
		}

		DominioNotificacaoTuberculoseIndDiabetes indDiabetes = (DominioNotificacaoTuberculoseIndDiabetes) row[56];
		if (indDiabetes != null) {
			dados.setIndDiabetes(indDiabetes.getCodigo());
		}

		DominioNotificacaoTuberculoseIndDoencaMental indDoencaMental = (DominioNotificacaoTuberculoseIndDoencaMental) row[57];
		if (indDoencaMental != null) {
			dados.setIndDoencaMental(indDoencaMental.getCodigo());
		}
	}
	
	private void montarCamposEnumObterInformacoesPdfSinanParte4(Object[] row, GerarPDFSinanVO dados) {

		DominioNotificacaoTuberculoseIndAlcoolismo indAlcoolismo = (DominioNotificacaoTuberculoseIndAlcoolismo) row[58];
		if (indAlcoolismo != null) {
			dados.setIndAlcoolismo(indAlcoolismo.getCodigo());
		}

		DominioNotificacaoTuberculoseTabagismo indTabagismo = (DominioNotificacaoTuberculoseTabagismo) row[59];
		if (indTabagismo != null) {
			dados.setIndTabagismo(indTabagismo.getCodigo());
		}

		DominioNotificacaoTuberculoseDrogasIlicitas indDrogasIlicitas = (DominioNotificacaoTuberculoseDrogasIlicitas) row[60];
		if (indDrogasIlicitas != null) {
			dados.setIndDrogasIlicitas(indDrogasIlicitas.getCodigo());
		}

		DominioNotificacaoTuberculoseAgravoOutrasDoencas indOutrasDoencas = (DominioNotificacaoTuberculoseAgravoOutrasDoencas) row[61];
		if (indOutrasDoencas != null) {
			dados.setIndOutrasDoencas(indOutrasDoencas.getCodigo());
		}

		DominioNotificacaoTuberculoseBaciloscopiaEscarro baciloscopiaEscarro = (DominioNotificacaoTuberculoseBaciloscopiaEscarro) row[63];
		if (baciloscopiaEscarro != null) {
			dados.setBaciloscopiaEscarro(baciloscopiaEscarro.getCodigo() + " - " + baciloscopiaEscarro.getDescricao());
		}

		DominioNotificacaoTuberculoseRaioXTorax raioxTorax = (DominioNotificacaoTuberculoseRaioXTorax) row[64];
		if (raioxTorax != null) {
			dados.setRaioxTorax(raioxTorax.getCodigo() + " - " + raioxTorax.getDescricao());
		}
	}
	
	private void montarCamposEnumObterInformacoesPdfSinanParte5(Object[] row, GerarPDFSinanVO dados) {

		DominioNotificacaoTuberculoseHiv hiv = (DominioNotificacaoTuberculoseHiv) row[65];
		if (hiv != null) {
			dados.setHiv(hiv.getCodigo() + " - " + hiv.getDescricao());
		}

		DominioNotificacaoTuberculoseAntirretroviral indAntiRetroviral = (DominioNotificacaoTuberculoseAntirretroviral) row[66];
		if (indAntiRetroviral != null) {
			dados.setIndAntiRetroviral(indAntiRetroviral.getCodigo() + " - " + indAntiRetroviral.getDescricao());
		}

		DominioNotificacaoTuberculoseHistopatologia histopatologia = (DominioNotificacaoTuberculoseHistopatologia) row[67];
		if (histopatologia != null) {
			dados.setHistopatologia(histopatologia.getCodigo() + " - " + histopatologia.getDescricao());
		}

		DominioNotificacaoTuberculoseCulturaEscarro culturaEscarro = (DominioNotificacaoTuberculoseCulturaEscarro) row[68];
		if (culturaEscarro != null) {
			dados.setCulturaEscarro(culturaEscarro.getCodigo() + " - " + culturaEscarro.getDescricao());
		}

		DominioNotificacaoTuberculoseTMR indTmr = (DominioNotificacaoTuberculoseTMR) row[69];
		if (indTmr != null) {
			dados.setIndTmr(indTmr.getCodigo() + " - " + indTmr.getDescricao());
		}

		DominioNotificacaoTuberculoseSensibilidade indSensibilidade = (DominioNotificacaoTuberculoseSensibilidade) row[70];
		if (indSensibilidade != null) {
			dados.setIndSensibilidade(indSensibilidade.getCodigo() + " - " + indSensibilidade.getDescricao());
		}
	}

	private String montarCampoDescExtrapulmonarObterInformacoesPdfSinan(Object[] row) {

		StringBuilder descExtrapulmonar = new StringBuilder(44);

		DominioNotificacaoTuberculoseIndPleural indPleural = (DominioNotificacaoTuberculoseIndPleural) row[44];
		if (DominioNotificacaoTuberculoseIndPleural.PLEURAL.equals(indPleural)) {
			descExtrapulmonar.append(indPleural.getCodigo())
				.append(" - ")
				.append(indPleural.getDescricao());
		}
		
		DominioNotificacaoTuberculoseIndGangPerif indGangPerif = (DominioNotificacaoTuberculoseIndGangPerif) row[45];
		if (DominioNotificacaoTuberculoseIndGangPerif.GANG_PERIF.equals(indGangPerif)) {
			if (descExtrapulmonar.length() > 0) {
				descExtrapulmonar.append(", ");
			}

			descExtrapulmonar.append(indGangPerif.getCodigo())
				.append(" - ")
				.append(indGangPerif.getDescricao());
		}
		
		DominioNotificacaoTuberculoseIndGenitoUrinaria indGenitoUrinaria = (DominioNotificacaoTuberculoseIndGenitoUrinaria) row[46];
		if (DominioNotificacaoTuberculoseIndGenitoUrinaria.PLEURAL.equals(indGenitoUrinaria)) {
			if (descExtrapulmonar.length() > 0) {
				descExtrapulmonar.append(", ");
			}

			// A enum de domínio para este campo está retornando uma descrição diferente, conforme especificado na estória #45060,
			// porém no relatório é necessário que seja mostrado o nome correto do campo preenchido (Geniturinária).
			descExtrapulmonar.append(indGenitoUrinaria.getCodigo())
				.append(" - Geniturinária");
		}

		DominioNotificacaoTuberculoseIndOssea indOssea = (DominioNotificacaoTuberculoseIndOssea) row[47];
		if (DominioNotificacaoTuberculoseIndOssea.OSSEA.equals(indOssea)) {
			if (descExtrapulmonar.length() > 0) {
				descExtrapulmonar.append(", ");
			}

			descExtrapulmonar.append(indOssea.getCodigo())
				.append(" - ")
				.append(indOssea.getDescricao());
		}
		
		DominioNotificacaoTuberculoseIndOcular indOcular = (DominioNotificacaoTuberculoseIndOcular) row[48];
		if (DominioNotificacaoTuberculoseIndOcular.OCULAR.equals(indOcular)) {
			if (descExtrapulmonar.length() > 0) {
				descExtrapulmonar.append(", ");
			}

			descExtrapulmonar.append(indOcular.getCodigo())
				.append(" - ")
				.append(indOcular.getDescricao());
		}

		montarCampoDescExtrapulmonarObterInformacoesPdfSinanParte2(row, descExtrapulmonar);

		return descExtrapulmonar.toString();
	}
	
	private void montarCampoDescExtrapulmonarObterInformacoesPdfSinanParte2(Object[] row, StringBuilder descExtrapulmonar) {

		DominioNotificacaoTuberculoseIndMiliar indMiliar = (DominioNotificacaoTuberculoseIndMiliar) row[49];
		if (DominioNotificacaoTuberculoseIndMiliar.MILIAR.equals(indMiliar)) {
			if (descExtrapulmonar.length() > 0) {
				descExtrapulmonar.append(", ");
			}

			descExtrapulmonar.append(indMiliar.getCodigo())
				.append(" - ")
				.append(indMiliar.getDescricao());
		}

		DominioNotificacaoTuberculoseIndMeningite indMeningite = (DominioNotificacaoTuberculoseIndMeningite) row[50];
		if (DominioNotificacaoTuberculoseIndMeningite.MENINGITE.equals(indMeningite)) {
			if (descExtrapulmonar.length() > 0) {
				descExtrapulmonar.append(", ");
			}


			// A enum de domínio para este campo está retornando uma descrição diferente, conforme especificado na estória #45060,
			// porém no relatório é necessário que seja mostrado o nome correto do campo preenchido (Meningoencefálico).
			descExtrapulmonar.append(indMeningite.getCodigo())
				.append(" - Meningoencefálico");
		}

		DominioNotificacaoTuberculoseIndCutanea indCutanea = (DominioNotificacaoTuberculoseIndCutanea) row[51];
		if (DominioNotificacaoTuberculoseIndCutanea.CUTANEA.equals(indCutanea)) {
			if (descExtrapulmonar.length() > 0) {
				descExtrapulmonar.append(", ");
			}

			descExtrapulmonar.append(indCutanea.getCodigo())
				.append(" - ")
				.append(indCutanea.getDescricao());
		}

		DominioNotificacaoTuberculoseIndLaringea indLaringea = (DominioNotificacaoTuberculoseIndLaringea) row[52];
		if (DominioNotificacaoTuberculoseIndLaringea.LARINGEA.equals(indLaringea)) {
			if (descExtrapulmonar.length() > 0) {
				descExtrapulmonar.append(", ");
			}

			descExtrapulmonar.append(indLaringea.getCodigo())
				.append(" - ")
				.append(indLaringea.getDescricao());
		}

		DominioNotificacaoTuberculoseIndOutraExtraPulmonar indOutraExtrapulmonar = (DominioNotificacaoTuberculoseIndOutraExtraPulmonar) row[53];
		if (DominioNotificacaoTuberculoseIndOutraExtraPulmonar.OUTRAS.equals(indOutraExtrapulmonar)
				|| DominioNotificacaoTuberculoseIndOutraExtraPulmonar.OUTRAS_NOVA_VERSAO.equals(indOutraExtrapulmonar)) {
			if (descExtrapulmonar.length() > 0) {
				descExtrapulmonar.append(", ");
			}

			// A enum de domínio para este campo está retornando uma descrição diferente, conforme especificado na estória #45060,
			// porém no relatório é necessário que seja mostrado o nome correto do campo preenchido (Outra).
			descExtrapulmonar.append(indOutraExtrapulmonar.getCodigo())
				.append(" - Outra: ").append((String) row[54]);
		}
	}
	
	private String montarCampoDescPopulEspecObterInformacoesPdfSinan(Object[] row) {

		StringBuilder descPopulacaoEspecial = new StringBuilder(120);

		DominioNotificacaoTuberculoseLiberdade indLiberdade = (DominioNotificacaoTuberculoseLiberdade) row[38];
		descPopulacaoEspecial.append(indLiberdade.getCodigo())
			.append(" - ")
			.append("População Privada de Liberdade");
		
		DominioNotificacaoTuberculoseProfSaude indProfSaude = (DominioNotificacaoTuberculoseProfSaude) row[39];
		descPopulacaoEspecial.append(", ");

		descPopulacaoEspecial.append(indProfSaude.getCodigo())
			.append(" - ")
			.append("Profissional de Saúde");
		
		DominioNotificacaoTuberculoseSitRua indSitRua = (DominioNotificacaoTuberculoseSitRua) row[40];
		descPopulacaoEspecial.append(", ");
		descPopulacaoEspecial.append(indSitRua.getCodigo())
			.append(" - ")
			.append("População em Situação de Rua");
		
		DominioNotificacaoTuberculoseImigrantes indImigrantes = (DominioNotificacaoTuberculoseImigrantes) row[41];
		descPopulacaoEspecial.append(", ");
		descPopulacaoEspecial.append(indImigrantes.getCodigo())
			.append(" - ")
			.append("Imigrante");
		
		return descPopulacaoEspecial.toString();
	}
	
	private String montarCampoDescDoeancasAgravosAssociadosPdfSinan(Object[] row) {

		StringBuilder descDoeancasAgravosAssociados = new StringBuilder(120);

		DominioNotificacaoTuberculoseIndAids indAids = (DominioNotificacaoTuberculoseIndAids) row[55];
		descDoeancasAgravosAssociados.append(indAids.getCodigo())
		.append(" - ")
		.append("Aids");
		
		DominioNotificacaoTuberculoseIndDiabetes indDiabetes = (DominioNotificacaoTuberculoseIndDiabetes) row[56];
		descDoeancasAgravosAssociados.append(", ");

		descDoeancasAgravosAssociados.append(indDiabetes.getCodigo())
		.append(" - ")
		.append("Diabetes");
		
		DominioNotificacaoTuberculoseIndDoencaMental indDoencaMental = (DominioNotificacaoTuberculoseIndDoencaMental) row[57];
		descDoeancasAgravosAssociados.append(", ");

		descDoeancasAgravosAssociados.append(indDoencaMental.getCodigo())
		.append(" - ")
		.append(indDoencaMental.getDescricaoFixa());
		
		DominioNotificacaoTuberculoseIndAlcoolismo indAlcoolismo = (DominioNotificacaoTuberculoseIndAlcoolismo) row[58];
		descDoeancasAgravosAssociados.append(", ");
		
		descDoeancasAgravosAssociados.append(indAlcoolismo.getCodigo())
		.append(" - ")
		.append(indAlcoolismo.getDescricaoFixa());
		
		montarCampoDescDoeancasAgravosAssociadosPdfSinan2(row, descDoeancasAgravosAssociados);
		
		return descDoeancasAgravosAssociados.toString();
	}

	private void montarCampoDescDoeancasAgravosAssociadosPdfSinan2(
			Object[] row, StringBuilder descDoeancasAgravosAssociados) {
		DominioNotificacaoTuberculoseTabagismo indTabagismo = (DominioNotificacaoTuberculoseTabagismo) row[59];
		descDoeancasAgravosAssociados.append(", ");
		
		descDoeancasAgravosAssociados.append(indTabagismo.getCodigo())
		.append(" - ")
		.append(indTabagismo.getDescricaoFixa());
		
		DominioNotificacaoTuberculoseDrogasIlicitas indDrogasIlicitas = (DominioNotificacaoTuberculoseDrogasIlicitas) row[60];
		descDoeancasAgravosAssociados.append(", ");
		
		descDoeancasAgravosAssociados.append(indDrogasIlicitas.getCodigo())
		.append(" - ")
		.append(indDrogasIlicitas.getDescricaoFixa());
		
		DominioNotificacaoTuberculoseAgravoOutrasDoencas indOutrasDoencas = (DominioNotificacaoTuberculoseAgravoOutrasDoencas) row[61];
		if (indOutrasDoencas != null) {
			descDoeancasAgravosAssociados.append(", ");
			descDoeancasAgravosAssociados.append(indOutrasDoencas.getCodigo())
			.append(" - ")
			.append((String) row[62]);
		}
	}

}
