package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import java.util.Date;

import br.gov.mec.aghu.faturamento.stringtemplate.NullToZeroHelper;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioCpfCnsCnpjCnes;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioModIntern;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioStMudaproc;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;


public class DefaultRegistroAihEspecificacao
		extends
			AbstractRegistroAihEspecificacao {

	private final DominioModIntern modIntern;
	private final Short seqAih5;
	private final Long aihProx;
	private final Long aihAnt;
	private final Date dtEmissao;
	private final Date dtIntern;
	private final Date dtSaida;
	private final Long procSolicitado;
	private final DominioStMudaproc stMudaproc;
	private final Long procRealizado;
	private final Byte carIntern;
	private final Byte motSaida;
	private final DominioCpfCnsCnpjCnes identMedSol;
	private final Long docMedSol;
	private final DominioCpfCnsCnpjCnes identMedResp;
	private final Long docMedResp;
	private final DominioCpfCnsCnpjCnes identDirclinico;
	private final Long docDirclinico;
	private final DominioCpfCnsCnpjCnes identAutoriz;
	private final Long docAutoriz;
	private final String diagPrin;
	private final String diagSec;
	private final String diagCompl;
	private final String diagObito;
	private final String codSolLib;

	@SuppressWarnings("PMD.ExcessiveParameterList")
	public DefaultRegistroAihEspecificacao(final AttributeFormatPairRendererHelper rendererHelper,
			final DominioModIntern modIntern,
			final Short seqAih5,
			final Long aihProx,
			final Long aihAnt,
			final Date dtEmissao,
			final Date dtIntern,
			final Date dtSaida,
			final Long procSolicitado,
			final DominioStMudaproc stMudaproc,
			final Long procRealizado,
			final Byte carIntern,
			final Byte motSaida,
			final DominioCpfCnsCnpjCnes identMedSol,
			final Long docMedSol,
			final DominioCpfCnsCnpjCnes identMedResp,
			final Long docMedResp,
			final DominioCpfCnsCnpjCnes identDirclinico,
			final Long docDirclinico,
			final DominioCpfCnsCnpjCnes identAutoriz,
			final Long docAutoriz,
			final String diagPrin,
			final String diagSec,
			final String diagCompl,
			final String diagObito,
			final String codSolLib) {

		super(rendererHelper);
		this.modIntern = modIntern;
		this.seqAih5 = seqAih5;
		this.aihProx = aihProx;
		this.aihAnt = aihAnt;
		this.dtEmissao = dtEmissao;
		this.dtIntern = dtIntern;
		this.dtSaida = dtSaida;
		this.procSolicitado = procSolicitado;
		this.stMudaproc = stMudaproc;
		this.procRealizado = procRealizado;
		this.carIntern = carIntern;
		this.motSaida = motSaida;
		this.identMedSol = identMedSol;
		this.docMedSol = docMedSol;
		this.identMedResp = identMedResp;
		this.docMedResp = docMedResp;
		this.identDirclinico = identDirclinico;
		this.docDirclinico = docDirclinico;
		this.identAutoriz = identAutoriz;
		this.docAutoriz = docAutoriz;
		this.diagPrin = diagPrin;
		this.diagSec = diagSec;
		this.diagCompl = diagCompl;
		this.diagObito = diagObito;
		this.codSolLib = codSolLib;
	}

	@Override
	public DominioModIntern getModIntern() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.modIntern, this.isNullToZeroed());
	}

	@Override
	public Short getSeqAih5() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.seqAih5, this.isNullToZeroed());
	}

	@Override
	public Long getAihProx() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.aihProx, this.isNullToZeroed());
	}

	@Override
	public Long getAihAnt() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.aihAnt, this.isNullToZeroed());
	}

	@Override
	public Date getDtEmissao() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.dtEmissao, this.isNullToZeroed());
	}

	@Override
	public Date getDtIntern() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.dtIntern, this.isNullToZeroed());
	}

	@Override
	public Date getDtSaida() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.dtSaida, this.isNullToZeroed());
	}

	@Override
	public Long getProcSolicitado() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.procSolicitado, this.isNullToZeroed());
	}

	@Override
	public DominioStMudaproc getStMudaproc() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.stMudaproc, this.isNullToZeroed());
	}

	@Override
	public Long getProcRealizado() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.procRealizado, this.isNullToZeroed());
	}

	@Override
	public Byte getCarIntern() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.carIntern, this.isNullToZeroed());
	}

	@Override
	public Byte getMotSaida() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.motSaida, this.isNullToZeroed());
	}

	@Override
	public DominioCpfCnsCnpjCnes getIdentMedSol() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.identMedSol, this.isNullToZeroed());
	}

	@Override
	public Long getDocMedSol() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.docMedSol, this.isNullToZeroed());
	}

	@Override
	public DominioCpfCnsCnpjCnes getIdentMedResp() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.identMedResp, this.isNullToZeroed());
	}

	@Override
	public Long getDocMedResp() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.docMedResp, this.isNullToZeroed());
	}

	@Override
	public DominioCpfCnsCnpjCnes getIdentDirclinico() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.identDirclinico, this.isNullToZeroed());
	}

	@Override
	public Long getDocDirclinico() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.docDirclinico, this.isNullToZeroed());
	}

	@Override
	public DominioCpfCnsCnpjCnes getIdentAutoriz() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.identAutoriz, this.isNullToZeroed());
	}

	@Override
	public Long getDocAutoriz() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.docAutoriz, this.isNullToZeroed());
	}

	@Override
	public String getDiagPrin() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.diagPrin, this.isNullToZeroed());
	}

	@Override
	public String getDiagSec() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.diagSec, this.isNullToZeroed());
	}

	@Override
	public String getDiagCompl() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.diagCompl, this.isNullToZeroed());
	}

	@Override
	public String getDiagObito() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.diagObito, this.isNullToZeroed());
	}

	@Override
	public String getCodSolLib() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.codSolLib, this.isNullToZeroed());
	}

	@Override
	public String getDescricao() {

		return "solic: " + this.getProcSolicitado() + " realiz: " + this.getProcRealizado();
	}
}
